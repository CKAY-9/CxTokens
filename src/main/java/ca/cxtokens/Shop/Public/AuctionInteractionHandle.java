package ca.cxtokens.Shop.Public;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import ca.cxtokens.Storage;
import ca.cxtokens.CxTokens;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;
import ca.cxtokens.Shop.GlobalShop;

public class AuctionInteractionHandle implements Listener {
    /*
     * Double chests have 54 slots (D.Bs are used for the store)
     * 45 = bottom left slot
     * 53 = bottom right slot
     */
    private final int BACK_EXIT = 45;
    private final int NEXT_PAGE = 53;
    CxTokens tokens;

    public AuctionInteractionHandle(CxTokens tokens) {
        this.tokens = tokens;
    } 

    private void placeBid(int index, int page, Player player) {
        Item item = this.tokens.auctionHouse.auctionItems.get(index);
        TokenPlayer tokenPlayer = TokenPlayer.convertPlayerToTokenPlayer(player);

        if (tokenPlayer.getTokens() < Math.round(item.currentBid * Storage.config.getDouble("auction.bidIncreaseMultiplier", 1.25))) {
            player.sendMessage(Utils.formatText("&cYou need to have at least T$" + Math.round(item.currentBid * Storage.config.getDouble("auction.bidIncreaseMultiplier", 1.25)) + " to place a bid!"));
            return;
        }

        // reset previous bidder
        if (item.bidder != null) {
            TokenPlayer.convertPlayerToTokenPlayer(item.bidder).addTokens(item.currentBid, true);
            item.bidder.sendMessage(Utils.formatText("&eYour bid on " + item.seller.getName() + "'s " + item.item.getItemMeta().getDisplayName() + " has been beat out! (Page: " + (page + 1) + ")"));
        }
        
        // set new bidder
        item.bidder = player;
        item.currentBid = (int) Math.round(item.currentBid * Storage.config.getDouble("auction.bidIncreaseMultiplier", 1.25));
        player.sendMessage(Utils.formatText("&aYour bid has been placed for T$" + item.currentBid));
        item.seller.sendMessage(Utils.formatText("&aYour " + item.item.getType().name() + " has recieved a new bid of T$" + item.currentBid));
    }

    @EventHandler
    public void onAuctionClick(InventoryClickEvent e) {
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

        if (clicked == BACK_EXIT) {
            e.getView().close();

            // Exit
            if (currentPage == 0) {
                return;
            }

            // Go Back
            this.tokens.auctionHouse.openAuctionHouse((Player) e.getWhoClicked(), (currentPage - 1));
            return;
        }

        if (clicked == NEXT_PAGE && currentPage < Math.round(this.tokens.auctionHouse.auctionItems.size() / GlobalShop.MAX_ITEMS_PER_PAGE)) {
            e.getView().close();
            this.tokens.auctionHouse.openAuctionHouse((Player) e.getWhoClicked(), (currentPage + 1));
            return;
        }

        int index = clicked + (GlobalShop.MAX_ITEMS_PER_PAGE * currentPage);

        if (clicked >= GlobalShop.MAX_ITEMS_PER_PAGE || clicked > (this.tokens.auctionHouse.auctionItems.size() - (GlobalShop.MAX_ITEMS_PER_PAGE * currentPage))) {
            return;
        }

        placeBid(index, currentPage, (Player) e.getWhoClicked());
        e.getView().close();
    }
}
