package ca.cxtokens.Events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ca.cxtokens.Storage;
import ca.cxtokens.CxTokens;
import ca.cxtokens.TokenPlayer;

public class MiscEvents {
    private CxTokens tokens;
    public Lottery lottery;

    public MiscEvents(CxTokens tokens) {
        this.tokens = tokens;
        this.tokens.getLogger().info("Registering Misc Events");
        if (Storage.config.getBoolean("routineTokens.enabled", true)) {
            int timeBetweenRoutine = 20 * Storage.config.getInt("routineTokens.waitTimeInSeconds", 300);
            this.tokens.getLogger().info("Registering routine tokens");
            this.tokens.getServer().getScheduler().scheduleSyncRepeatingTask(this.tokens, new Runnable() {
                @Override
                public void run() {
                    int tokenAmount = Storage.config.getInt("routineTokens.amountOfTokens", 50);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        TokenPlayer.getTokenPlayer(tokens, p)
                            .addTokens(tokenAmount, false);
                    }
                }
            }, timeBetweenRoutine, timeBetweenRoutine);
        }

        if (Storage.config.getBoolean("lottery.enabled", true)) {
            lottery = new Lottery(this.tokens);
            int timeBetweenLottery = 20 * Storage.config.getInt("lottery.waitTimeInSeconds", 1800);
            this.tokens.getLogger().info("Registering token lottery");
            this.tokens.getServer().getScheduler().scheduleSyncRepeatingTask(
                this.tokens, 
                lottery,
                timeBetweenLottery, 
                timeBetweenLottery
            );
        }
    }
}
