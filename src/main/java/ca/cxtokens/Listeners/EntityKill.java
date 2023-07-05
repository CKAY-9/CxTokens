package ca.cxtokens.Listeners;

import java.util.Random;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;

public class EntityKill implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityKill(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) {
            return;
        }

        if (!Storage.config.getBoolean("mobRewards.enabled", true)) {
            return;
        }

        int minAmount = Storage.config.getInt("mobRewards.minReward", 5);
        int maxAmount = Storage.config.getInt("mobRewards.maxRewards", 10);
        Random rand = new Random();
        int giveAmount = rand.nextInt(minAmount, maxAmount + 1);

        TokenPlayer.convertPlayerToTokenPlayer(e.getEntity().getKiller()).addTokens(giveAmount, false);
    }
}
