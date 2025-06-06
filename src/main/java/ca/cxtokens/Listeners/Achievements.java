package ca.cxtokens.Listeners;

import java.util.Random;

import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;

public class Achievements implements Listener {
    private CxTokens tokens;

    public Achievements(CxTokens tokens) {
        this.tokens = tokens;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAchievementEarned(PlayerAdvancementDoneEvent event) {
        if (!Storage.config.getBoolean("achievements.enabled", true)) {
            return;
        }

        Player player = event.getPlayer();
        Advancement advancement = event.getAdvancement();
        String advancement_key = advancement.getKey().getKey();

        if (Storage.config.getBoolean("achievements.disableRecipes", true) && advancement_key.contains("recipes/")) {
            return;
        }

        long min_amount = Storage.config.getLong("achievements.minReward", 50L);
        long max_amount = Storage.config.getLong("achievements.maxReward", 750L);

        if (Storage.config.isSet("achievements.custom." + advancement_key)) {
            min_amount = Storage.config.getLong("achievements.custom." + advancement_key + ".minReward", 50L);
            max_amount = Storage.config.getLong("achievements.custom." + advancement_key + ".maxReward", 750L);
        }

        Random rand = new Random();
        float maxPercent = rand.nextFloat();
        long giveAmount = Math.max(min_amount, Math.round(max_amount * maxPercent));

        TokenPlayer.getTokenPlayer(this.tokens, player).addTokens(giveAmount, false);
    }
}
