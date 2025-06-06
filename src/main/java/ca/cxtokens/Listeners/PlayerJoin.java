package ca.cxtokens.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import ca.cxtokens.CxTokens;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

import org.bukkit.event.EventPriority;

public class PlayerJoin implements Listener {
    private CxTokens tokens;
    
    public PlayerJoin(CxTokens tokens) {
        this.tokens = tokens;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerJoinEvent(PlayerJoinEvent event) {
        Player ply = event.getPlayer();
        TokenPlayer token = TokenPlayer.getTokenPlayer(this.tokens, ply);
        ply.sendMessage(Utils.formatText("&aYour current balance is &a&l" + CxTokens.currency + token.getTokens()));
    }
} 
