package ca.cxtokens.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

public class PlayerDeath implements Listener {
    @EventHandler
    public void playerBountyHandle(PlayerDeathEvent e) {
        TokenPlayer player = TokenPlayer.convertPlayerToTokenPlayer(e.getEntity());

        if (!player.hasBounty()) {
            return;
        }

        TokenPlayer killer = TokenPlayer.convertPlayerToTokenPlayer(e.getEntity().getKiller());

        boolean isSelf = killer.ply.getUniqueId().equals(player.ply.getUniqueId());
        if (isSelf && !Storage.config.getBoolean("bounty.allowSelfBounty", true)) {
            return;
        }

        // data overwrites itself if the player is itself
        if (isSelf) {
            player.addTokens(player.getBounty(), true);
        } else {
            killer.addTokens(player.getBounty(), true);
        }
        
        e.setDeathMessage("");
        Bukkit.broadcastMessage(Utils.formatText("&a" + killer.ply.getName() + " claimed the bounty on " + player.ply.getName() + " for " + CxTokens.currency + player.getBounty()));

        player.removeBounty();
    }
}
