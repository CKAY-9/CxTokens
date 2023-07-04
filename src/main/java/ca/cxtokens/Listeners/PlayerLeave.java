package ca.cxtokens.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import ca.cxtokens.CxTokens;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Shop.Public.Item;

public class PlayerLeave implements Listener {
    private CxTokens tokens;
    public PlayerLeave(CxTokens tokens) {
        this.tokens = tokens;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void lotteryLeaveCheck(PlayerQuitEvent e) {
        if (this.tokens.events.lottery.running) {
            this.tokens.events.lottery.removePlayerFromLottery(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void auctionLeaveCheck(PlayerQuitEvent e) {
        for (int i = 0; i < this.tokens.auctionHouse.auctionItems.size(); i++) {
            Item item = this.tokens.auctionHouse.auctionItems.get(i);
            if (item.seller.getUniqueId() == e.getPlayer().getUniqueId()) {
                // remove item and give money back to bidder
                item.seller.getInventory().addItem(item.item);
                if (item.bidder != null) {
                    TokenPlayer.convertPlayerToTokenPlayer(item.bidder).addTokens(item.currentBid, true);
                }
                this.tokens.auctionHouse.auctionItems.remove(i);
                break;
            }
            if (item.bidder.getUniqueId() == e.getPlayer().getUniqueId()) {
                // set bidder to null
                item.bidder = null;
                TokenPlayer.convertPlayerToTokenPlayer(e.getPlayer()).addTokens(item.currentBid, true);
                break;
            }
        }
    }
}
