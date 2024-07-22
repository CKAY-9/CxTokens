package ca.cxtokens.Listeners;

import java.util.Random;

import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;

public class Achievements implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onAchievementEarned(PlayerAdvancementDoneEvent event) {
        if (!Storage.config.getBoolean("achievements.enabled", true)) {
            return;
        }

        Player player = event.getPlayer();
        Advancement advancement = event.getAdvancement();
        String advancement_key = advancement.getKey().getKey(); // TODO: add specific rewards for advancements

        long min_amount = Storage.config.getLong("achievements.minReward", 50L);
        long max_amount = Storage.config.getLong("achievements.maxReward", 750L);
        Random rand = new Random();
        long give_amount = rand.nextLong(min_amount, max_amount + 1);
        TokenPlayer.convertPlayerToTokenPlayer(player).addTokens(give_amount, false);
    }
}
