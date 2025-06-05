package ca.cxtokens.Shop.Auction;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ca.cxtokens.Storage;
import ca.cxtokens.CxTokens;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;
import ca.cxtokens.Shop.GlobalShop;

public class AuctionHouse {
    CxTokens tokens;
    // viewers are used for refreshing pages and other things related to the auction house
    public ArrayList<Viewer> viewers = new ArrayList<>();
    public ArrayList<Item> auctionItems = new ArrayList<>();

    public AuctionHouse(CxTokens tokens) {
        this.tokens = tokens;

        // Sweeping goes over the items in the house and either remove them, continue, or sell them to the highest bidder
        this.tokens.getServer().getScheduler().scheduleSyncRepeatingTask(this.tokens, new Runnable() {
            @Override
            public void run() {
                tokens.getLogger().info("Running Auction House sweep...");

                for (int i = 0; i < auctionItems.size(); i++) {
                    Item item = auctionItems.get(i);
                    item.sweepsUntilComplete--;

                    if (item.sweepsUntilComplete < 0) {
                        item.sold = true;
                        boolean flag = true;
                        
                        // Clear item or give to player
                        if (item.bidder == null) {
                            if (item.seller.getInventory().firstEmpty() == -1) {
                                // wait for inventory to have space
                                flag = false;
                                continue;
                            }

                            // remove
                            item.seller.sendMessage(Utils.formatText("&cNo one bought your item on the auction house. Returning..."));
                            item.seller.getInventory().addItem(item.item);
                        } else {
                            if (item.bidder.getInventory().firstEmpty() == -1) {
                                // wait for inventory to have space
                                flag = false;
                                continue;
                            }

                            // give
                            item.seller.sendMessage(Utils.formatText("&aYour item has been sold for &a&l" + CxTokens.currency + item.currentBid + "&r&a on the Auction House!"));
                            TokenPlayer.convertPlayerToTokenPlayer(item.seller).addTokens(item.currentBid, true);

                            item.bidder.sendMessage(Utils.formatText("&aSuccessfully bought an item off the Auction House for &a&l" + CxTokens.currency + item.currentBid));
                            item.bidder.getInventory().addItem(item.item);
                        } 
                        if (flag) auctionItems.remove(i);
                    }
                }
                tokens.getLogger().info("Finished Auction House sweep!");

                updateViewers();
            }
        }, 20 * 60, 20 * 60);
    }

    public void updateViewers() {
        if (viewers.size() <= 0) return;

        tokens.getLogger().info("Updating auction house viewers...");
        for (Viewer viewer : viewers) {
            viewer.openPage(viewer.page);
        }
        tokens.getLogger().info("Updated auction house viewers!");
    }

    public Viewer getViewerFromPlayer(Player p) {
        for (Viewer v : this.viewers) {
            if (v.player.getUniqueId().toString().equals(p.getUniqueId().toString())) {
                return v;
            }
        }
        return null;
    }

    public void openAuctionHouse(Player player, int pageIndex) {
        Inventory auctionInv = Bukkit.createInventory(null, 54, Utils.formatText("&a&lAuction House - Page " + (pageIndex + 1)));
        auctionInv.clear();

        // read Shop/Static/Store.java for why this is this
        int start = pageIndex * GlobalShop.MAX_ITEMS_PER_PAGE;
        int limit = start + GlobalShop.MAX_ITEMS_PER_PAGE;
        if (this.auctionItems.size() < GlobalShop.MAX_ITEMS_PER_PAGE || limit >= this.auctionItems.size()) {
            limit = this.auctionItems.size();
        }

        int storageIndex = 0;
        for (int i = (0 + start); i < limit; i++) {
            if (storageIndex == GlobalShop.MAX_ITEMS_PER_PAGE) {
                break;
            }

            Item temp = this.auctionItems.get(i);
            if (temp.sold) continue;

            ItemStack stack = temp.item.clone();
            ItemMeta meta = stack.getItemMeta();

            ArrayList<String> lore = new ArrayList<>();
            lore.add(Utils.formatText("&aCurrent Bid: &l" + CxTokens.currency + temp.currentBid));
            lore.add(Utils.formatText("&aSeller: &l" + temp.seller.getName()));
            lore.add(Utils.formatText("&aTime Remaining: &l" + temp.sweepsUntilComplete + "m"));
            lore.add(Utils.formatText("&cPlace Bid: &l" + CxTokens.currency + Math.round(temp.currentBid * Storage.config.getDouble("auction.bidIncreaseMultiplier", 1.25))));
            meta.setLore(lore);

            stack.setItemMeta(meta);

            auctionInv.setItem(storageIndex, stack);
            storageIndex++;
        }

        ItemStack backOrExit = new ItemStack(Material.RED_CONCRETE, 1);
        ItemMeta boeMeta = backOrExit.getItemMeta();
        if (pageIndex == 0) {
            boeMeta.setDisplayName(Utils.formatText("&cExit"));
        } else {
            boeMeta.setDisplayName(Utils.formatText("&cBack to page " + (pageIndex)));
        }
        backOrExit.setItemMeta(boeMeta);
        auctionInv.setItem(45, backOrExit);

        if (pageIndex < Math.round(this.auctionItems.size() / GlobalShop.MAX_ITEMS_PER_PAGE)) {
            ItemStack next = new ItemStack(Material.GREEN_CONCRETE, 1);
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.setDisplayName(Utils.formatText("&aGo to page " + (pageIndex + 2))); // + 2 is just so the pages go 1, 2, 3,... instead of 0, 1, 2,...
            next.setItemMeta(nextMeta);
            auctionInv.setItem(53, next);
        }

        player.openInventory(auctionInv);
    }

    public void sellItemOnHouse(TokenPlayer player, long sellPrice) {
        if (player.ply.getInventory().getItemInMainHand().getType() == Material.AIR) {
            player.ply.sendMessage(Utils.formatText("&cYou need to have an item in your main hand to sell!"));
            return;
        }

        if (sellPrice < 0) {
            player.ply.sendMessage(Utils.formatText("&cItems cannot have a negative price!"));
            return;
        }

        // .clone() is needed as removing the item sets the auction item to be AIR
        ItemStack itemToSell = player.ply.getInventory().getItemInMainHand().clone();

        this.auctionItems.add(new Item(player.ply, sellPrice, itemToSell));
        player.ply.getInventory().getItemInMainHand().setAmount(0);
        player.ply.sendMessage(Utils.formatText("&aYour item is now on the Auction House!"));

        this.updateViewers();
    }
}
