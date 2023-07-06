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

        long minAmount = Storage.config.getLong("mobRewards.minReward", 5L);
        long maxAmount = Storage.config.getLong("mobRewards.maxRewards", 10L);
        Random rand = new Random();
        long giveAmount = rand.nextLong(minAmount, maxAmount + 1);

        TokenPlayer.convertPlayerToTokenPlayer(e.getEntity().getKiller()).addTokens(giveAmount, false);
    }
}
