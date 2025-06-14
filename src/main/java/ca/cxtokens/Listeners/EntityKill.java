package ca.cxtokens.Listeners;

import java.util.Random;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;

public class EntityKill implements Listener {
    private CxTokens tokens;

    public EntityKill(CxTokens tokens) {
        this.tokens = tokens;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityKill(EntityDeathEvent e) {
        if (!Storage.config.getBoolean("mobRewards.enabled", true)) {
            return;
        }

        LivingEntity entity = e.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) {
            return;
        }

        long minAmount = Storage.config.getLong("mobRewards.minReward", 5L);
        long maxAmount = Storage.config.getLong("mobRewards.maxReward", 10L);

        String ent_name = entity.getName();
        if (Storage.config.isSet("mobRewards.custom." + ent_name)) {
            minAmount = Storage.config.getLong("mobRewards.custom." + ent_name + ".minReward", 5L);
            maxAmount = Storage.config.getLong("mobRewards.custom." + ent_name + ".maxReward", 10L);
        }

        Random rand = new Random();
        float maxPercent = rand.nextFloat();
        long giveAmount = Math.max(minAmount, Math.round(maxAmount * maxPercent));

        TokenPlayer.getTokenPlayer(this.tokens, e.getEntity().getKiller()).addTokens(giveAmount, false);
    }
}
