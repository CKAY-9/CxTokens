package ca.camerxn.cxtokens.Events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ca.camerxn.cxtokens.Config;
import ca.camerxn.cxtokens.CxTokens;
import ca.camerxn.cxtokens.TokenPlayer;

public class MiscEvents {
    private CxTokens tokens;

    public Lottery lottery;

    public MiscEvents(CxTokens tokens) {
        this.tokens = tokens;
        this.tokens.getLogger().info("Registering Misc Events");

        if (Config.config.getBoolean("routineTokens.enabled", true)) {
            int timeBetweenRoutine = 20 * Config.config.getInt("routineTokens.waitTimeInSeconds", 300);

            this.tokens.getLogger().info("Registering routine tokens");
            this.tokens.getServer().getScheduler().scheduleSyncRepeatingTask(this.tokens, new Runnable() {
                @Override
                public void run() {
                    int tokenAmount = Config.config.getInt("routineTokens.amountOfTokens", 50);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        TokenPlayer.convertPlayerToTokenPlayer(p)
                            .addTokens(tokenAmount, false);;
                    }
                }
            }, timeBetweenRoutine, timeBetweenRoutine);
        }
        if (Config.config.getBoolean("lottery.enabled", true)) {
            lottery = new Lottery(this.tokens);
            int timeBetweenLottery = 20 * Config.config.getInt("lottery.waitTimeInSeconds", 1800);
    
            this.tokens.getLogger().info("Registering token lottery");
            this.tokens.getServer().getScheduler().scheduleSyncRepeatingTask(this.tokens, 
                lottery,
                timeBetweenLottery, 
                timeBetweenLottery);
        }
    }
}
