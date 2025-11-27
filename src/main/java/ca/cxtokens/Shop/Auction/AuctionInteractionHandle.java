package ca.cxtokens.Shop.Auction;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

import ca.cxtokens.Storage;
import ca.cxtokens.CxTokens;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;
import ca.cxtokens.Shop.GlobalShop;

public class AuctionInteractionHandle implements Listener {
    CxTokens tokens;

    public AuctionInteractionHandle(CxTokens tokens) {
        if (!Storage.config.getBoolean("auction.enabled", true)) {
            return;
        }

        this.tokens = tokens;
    } 

    private void placeBid(int index, int page, Player player) {
        if (!Storage.config.getBoolean("auction.enabled", true)) {
            return;
        }

        Item item = this.tokens.auctionHouse.auctionItems.get(index);
        TokenPlayer tokenPlayer = TokenPlayer.getTokenPlayer(this.tokens, player);

        if (tokenPlayer.ply.getUniqueId().toString().equals(item.seller.getUniqueId().toString())) {
            player.sendMessage(Utils.formatText("&cYou cannot bid on your own item!"));
            return;
        }

        if (tokenPlayer.getTokens() < Math.round(item.currentBid * Storage.config.getDouble("auction.bidIncreaseMultiplier", 1.25))) {
            player.sendMessage(Utils.formatText("&cYou need to have at least &c&l" + CxTokens.currency + Math.round(item.currentBid * Storage.config.getDouble("auction.bidIncreaseMultiplier", 1.25)) + "&r&c to place a bid!"));
            return;
        }

        // reset previous bidder
        if (item.bidder != null) {
            TokenPlayer.getTokenPlayer(this.tokens, item.bidder).addTokens(item.currentBid, true);
            item.bidder.sendMessage(Utils.formatText("&eYour bid on " + item.seller.getName() + "'s &e&l" + item.item.getItemMeta().getDisplayName() + "&r&e has been beat out! (Page: &e&l" + (page + 1) + "&r&e)"));
        }
        
        // set new bidder
        item.bidder = player;
        item.currentBid = (long) Math.round(item.currentBid * Storage.config.getDouble("auction.bidIncreaseMultiplier", 1.25));
        player.sendMessage(Utils.formatText("&aYour bid has been placed for &a&l" + CxTokens.currency + item.currentBid));
        tokenPlayer.subtractTokens(item.currentBid, false);
        item.seller.sendMessage(Utils.formatText("&aYour &a&l" + item.item.getType().name() + "&r&a has recieved a new bid of &a&l" + CxTokens.currency + item.currentBid));

        this.tokens.auctionHouse.updateViewers();
    }

    @EventHandler
    public void onAuctionClick(InventoryClickEvent e) {
        if (!Storage.config.getBoolean("auction.enabled", true)) {
            return;
        }

        if (e.getClickedInventory() == null) {
            return;
        }
        if (e.getClickedInventory().getHolder() != null) {
            return;
        }
        if (e.getClickedInventory().getType() != InventoryType.CHEST) {
            return;
        }
        if (!e.getView().getTitle().contains("Auction")) {
            return;
        }

        e.setCancelled(true);

        int currentPage = Integer.parseInt(e.getView().getTitle().split(" ")[4]) - 1;
        int clicked = e.getSlot();

        if (clicked == Utils.LARGE_EXIT_PREVIOUS_SLOT) {
            e.getView().close();

            // Exit
            if (currentPage == 0) {
                return;
            }

            // Go Back
            this.tokens.auctionHouse.getViewerFromPlayer((Player) e.getWhoClicked()).prevPage();
            return;
        }

        if (clicked == Utils.LARGE_NEXT_PAGE_SLOT && currentPage < Math.round(this.tokens.auctionHouse.auctionItems.size() / GlobalShop.MAX_ITEMS_PER_PAGE)) {
            e.getView().close();
            this.tokens.auctionHouse.getViewerFromPlayer((Player) e.getWhoClicked()).nextPage();
            return;
        }

        int index = clicked + (GlobalShop.MAX_ITEMS_PER_PAGE * currentPage);

        if (clicked >= GlobalShop.MAX_ITEMS_PER_PAGE || clicked > (this.tokens.auctionHouse.auctionItems.size() - (GlobalShop.MAX_ITEMS_PER_PAGE * currentPage))) {
            return;
        }

        placeBid(index, currentPage, (Player) e.getWhoClicked());
        e.getView().close();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void auctionHouseClose(InventoryCloseEvent e) {
        if (!Storage.config.getBoolean("auction.enabled", true)) {
            return;
        }

        for (Viewer viewer : this.tokens.auctionHouse.viewers) {
            if (viewer.player.getUniqueId().toString().equals(e.getPlayer().getUniqueId().toString())) {
                this.tokens.auctionHouse.viewers.remove(viewer);
            }
        }
    }
}
