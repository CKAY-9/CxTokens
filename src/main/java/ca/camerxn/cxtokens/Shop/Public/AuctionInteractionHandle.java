package ca.camerxn.cxtokens.Shop.Public;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import ca.camerxn.cxtokens.Config;
import ca.camerxn.cxtokens.CxTokens;
import ca.camerxn.cxtokens.TokenPlayer;
import ca.camerxn.cxtokens.Utils;

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

    private void placeBid(int slot, int page, Player player) {
        Item item = this.tokens.auctionHouse.auctionItems.get(slot + (page * 36));
        TokenPlayer tokenPlayer = TokenPlayer.convertPlayerToTokenPlayer(player);

        if (tokenPlayer.getTokens() < Math.round(item.currentBid * Config.config.getDouble("auction.bidIncreaseMultiplier", 1.25))) {
            player.sendMessage(Utils.formatText("&cYou need to have at least T$" + Math.round(item.currentBid * Config.config.getDouble("auction.bidIncreaseMultiplier", 1.25)) + " to place a bid!"));
            return;
        }

        // reset previous bidder
        if (item.bidder != null) {
            TokenPlayer.convertPlayerToTokenPlayer(item.bidder).addTokens(item.currentBid, true);
            item.bidder.sendMessage(Utils.formatText("&eYour bid on " + item.seller.getName() + "'s " + item.item.getItemMeta().getDisplayName() + " has been beat out! (Page: " + page + ")"));
        }
        
        // set new bidder
        item.bidder = player;
        item.currentBid = (int) Math.round(item.currentBid * Config.config.getDouble("auction.bidIncreaseMultiplier", 1.25));
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

        if (clicked == NEXT_PAGE) {
            e.getView().close();
            this.tokens.auctionHouse.openAuctionHouse((Player) e.getWhoClicked(), (currentPage + 1));
            return;
        }

        placeBid(clicked, currentPage, (Player) e.getWhoClicked());
        e.getView().close();
    }
}