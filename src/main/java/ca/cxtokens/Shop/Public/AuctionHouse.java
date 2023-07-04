package ca.cxtokens.Shop.Public;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ca.cxtokens.Config;
import ca.cxtokens.CxTokens;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

public class AuctionHouse {
    CxTokens tokens;
    private int sweepID = 0;
    public ArrayList<Item> auctionItems = new ArrayList<>();

    public AuctionHouse(CxTokens tokens) {
        this.tokens = tokens;

        // Sweeping goes over the items in the house and either remove them, continue, or sell them to the highest bidder
        this.sweepID = this.tokens.getServer().getScheduler().scheduleSyncRepeatingTask(this.tokens, new Runnable() {
            @Override
            public void run() {
                tokens.getLogger().info("Running Auction House sweep...");

                for (int i = 0; i < auctionItems.size(); i++) {
                    Item item = auctionItems.get(i);
                    item.sweepsUntilComplete--;

                    if (item.sweepsUntilComplete < 0) {
                        // Clear item or give to player
                        if (item.bidder == null) {
                            // remove
                            item.seller.sendMessage(Utils.formatText("&cNo one bought your item on the auction house :("));
                            item.seller.getInventory().addItem(item.item);
                        } else {
                            // give
                            item.seller.sendMessage(Utils.formatText("&aYour item has been sold for T$" + item.currentBid + " on the Auction House!"));
                            TokenPlayer.convertPlayerToTokenPlayer(item.seller).addTokens(item.currentBid, true);

                            item.bidder.sendMessage(Utils.formatText("&aSuccessfully bought an item off the Auction House for T$" + item.currentBid));
                            item.bidder.getInventory().addItem(item.item);
                        }
                        auctionItems.remove(i);
                    }
                }
            }
        }, 20 * 60, 20 * 60);
    }

    public void openAuctionHouse(Player player, int pageIndex) {
        Inventory auctionInv = Bukkit.createInventory(null, 54, Utils.formatText("&a&lAuction House - Page " + (pageIndex + 1)));
        auctionInv.clear();

        /*
         * (pageIndex * 36) is used to "scroll" the pages in the auction house
         * Example: 
         *  pI = 0: get the first page until it's end (35)
         *  pI = 3: get the third page until it's end (~105)
         */
        for (int i = (0 + (pageIndex * 36)); i < this.auctionItems.size(); i++) {
            Item temp = this.auctionItems.get(i);

            ItemStack stack = temp.item.clone();
            ItemMeta meta = stack.getItemMeta();

            ArrayList<String> lore = new ArrayList<>();
            lore.add(Utils.formatText("&aCurrent Bid: &lT$" + temp.currentBid));
            lore.add(Utils.formatText("&aSeller: &l" + temp.seller.getName()));
            lore.add(Utils.formatText("&aTime Remaining: &l" + temp.sweepsUntilComplete + "m"));
            lore.add(Utils.formatText("&cPlace Bid: &lT$" + Math.round(temp.currentBid * Config.config.getDouble("auction.bidIncreaseMultiplier", 1.25))));
            meta.setLore(lore);

            stack.setItemMeta(meta);

            auctionInv.setItem(i, stack);
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

        if (pageIndex < (Math.round(this.auctionItems.size() / 36) - 1)) {
            ItemStack next = new ItemStack(Material.GREEN_CONCRETE, 1);
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.setDisplayName(Utils.formatText("&aGo to page " + (pageIndex + 2))); // + 2 is just so the pages go 1, 2, 3,... instead of 0, 1, 2,...
            next.setItemMeta(nextMeta);
            auctionInv.setItem(53, next);
        }

        player.openInventory(auctionInv);
    }

    public void sellItemOnHouse(TokenPlayer player, int sellPrice) {
        if (player.ply.getInventory().getItemInMainHand().getType() == Material.AIR) {
            player.ply.sendMessage(Utils.formatText("&cYou need to have an item in your main hand to sell!"));
            return;
        }

        // .clone() is needed as removing the item sets the auction item to be AIR
        ItemStack itemToSell = player.ply.getInventory().getItemInMainHand().clone();

        this.auctionItems.add(new Item(player.ply, sellPrice, itemToSell));
        player.ply.getInventory().getItemInMainHand().setAmount(0);
        player.ply.sendMessage(Utils.formatText("&aYour item is now on the Auction House!"));
    }
}
