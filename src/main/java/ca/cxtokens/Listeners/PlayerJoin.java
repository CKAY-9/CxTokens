package ca.cxtokens.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

import org.bukkit.event.EventPriority;

public class PlayerJoin implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoinEvent(PlayerJoinEvent event) {
        Player ply = event.getPlayer();
        TokenPlayer tempToken = TokenPlayer.convertPlayerToTokenPlayer(ply);
        ply.sendMessage(Utils.formatText("&aYour current token balance is T$" + tempToken.getTokens()));
    }
} 
