package ca.camerxn.cxtokens.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import ca.camerxn.cxtokens.Utils;
import ca.camerxn.cxtokens.Tokens.TokenPlayer;
import org.bukkit.event.EventPriority;

public class PlayerJoin implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void playerJoinEvent(PlayerJoinEvent event) {
        Player ply = event.getPlayer();
        TokenPlayer tempToken = TokenPlayer.convertPlayerToTokenPlayer(ply);
        ply.sendMessage(Utils.formatText("&aYour current token balance is T$" + tempToken.getTokens()));
    }
} 
