package ca.cxtokens.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

public class PlayerDeath implements Listener {
    private CxTokens tokens;

    public PlayerDeath(CxTokens tokens) {
        this.tokens = tokens;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void stealTokenHandle(PlayerDeathEvent e) {
        int percentage = Storage.config.getInt("player_death.steal_token_percentage", 33);
        if (percentage <= 0 || e.getEntity() == null || e.getEntity().getKiller() == null) {
            return;
        }

        TokenPlayer player = TokenPlayer.getTokenPlayer(this.tokens, e.getEntity());
        TokenPlayer killer = TokenPlayer.getTokenPlayer(this.tokens, e.getEntity().getKiller());
        boolean is_self = killer.ply.getUniqueId().equals(player.ply.getUniqueId());
        if (is_self) {
            return;
        }

        long tokens_to_steal = Math.round(player.getTokens() * ((double) percentage / 100));
        player.subtractTokens(tokens_to_steal, true);
        killer.addTokens(tokens_to_steal, true);

        player.ply.sendMessage(
            Utils.formatText(
                "&c&l" + killer.ply.getName() + "&r&c has stolen &c&l" + CxTokens.currency + "" + tokens_to_steal + " &r&cfrom you"));
        killer.ply.sendMessage(
            Utils.formatText(
                "&c&lYou &r&chave stolen &c&l" + CxTokens.currency + "" + tokens_to_steal + " &r&cfrom &c&l" + player.ply.getName()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerBountyHandle(PlayerDeathEvent e) {
        TokenPlayer player = TokenPlayer.getTokenPlayer(this.tokens, e.getEntity());
        if (!player.hasBounty()) {
            return;
        }

        TokenPlayer killer = TokenPlayer.getTokenPlayer(this.tokens, e.getEntity().getKiller());
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
