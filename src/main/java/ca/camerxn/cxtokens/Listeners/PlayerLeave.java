package ca.camerxn.cxtokens.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import ca.camerxn.cxtokens.CxTokens;

public class PlayerLeave implements Listener {
    private CxTokens tokens;
    public PlayerLeave(CxTokens tokens) {
        this.tokens = tokens;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (this.tokens.events.lottery.running) {
            this.tokens.events.lottery.removePlayerFromLottery(e.getPlayer());
        }
    }
}
