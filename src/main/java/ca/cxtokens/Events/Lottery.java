package ca.cxtokens.Events;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ca.cxtokens.Storage;
import ca.cxtokens.CxTokens;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

public class Lottery implements Runnable {
    private CxTokens tokens;
    int announcementTask = 0;
    int doTask = 0;

    public boolean running = false;
    public ArrayList<TokenPlayer> joinedPlayers;

    public Lottery(CxTokens tokens) {
        this.tokens = tokens;
        this.joinedPlayers = new ArrayList<TokenPlayer>();
    }

    // This used for disconnects because it will result in an error if 
    // the selected player is disconnected, and it will give money back to
    // the disconnected player
    public void removePlayerFromLottery(Player p) {
        for (TokenPlayer temp : this.joinedPlayers) {
            if (temp.ply == p) {
                temp.addTokens(Storage.config.getInt("lottery.entryConst", 150), true);
                joinedPlayers.remove(temp);
                break;
            }
        }
    }

    @Override
    public void run() {
        if (this.running) {
            this.tokens.getLogger().warning("Tried to start a lottery while one is already running!");
            return;
        }

        this.running = true;
        this.announcementTask = this.tokens.getServer().getScheduler().scheduleSyncRepeatingTask(this.tokens, new Runnable() {
            int currentIter = 0;
            
            @Override
            public void run() {
                currentIter++;
                if (currentIter > 4) {
                    Utils.getPlugin().getServer().getScheduler().cancelTask(announcementTask);
                    return;
                }

                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage("");
                    p.sendMessage(Utils.formatText("&a&l--------- LOTTERY ---------"));
                    p.sendMessage(Utils.formatText("&aType \"/tlottery\" to join in!"));
                    p.sendMessage(Utils.formatText("&aJoin cost: " + CxTokens.currency + Storage.config.getInt("lottery.entryCost", 150)));
                    p.sendMessage(Utils.formatText("&a&l--------- LOTTERY ---------"));
                    p.sendMessage("");
                }
            }
        }, 0, 20 * 15);

        this.doTask = this.tokens.getServer().getScheduler().scheduleSyncRepeatingTask(this.tokens, new Runnable() {
            @Override
            public void run() {
                Utils.getPlugin().getServer().getScheduler().cancelTask(doTask);
                running = false;
                if (joinedPlayers.size() <= 0) {
                    return;
                }

                TokenPlayer selected = joinedPlayers.get(new Random().nextInt(joinedPlayers.size()));
                selected.addTokens(joinedPlayers.size() * Storage.config.getInt("lottery.entryCost", 150), false);

                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage("");
                    p.sendMessage(Utils.formatText("&a&l--------- LOTTERY ---------"));
                    p.sendMessage(Utils.formatText("&a" + selected.ply.getName() + " has won the lottery!"));
                    p.sendMessage(Utils.formatText("&aThey won " + CxTokens.currency + (joinedPlayers.size() * Storage.config.getInt("lottery.entryCost", 150))));
                    p.sendMessage(Utils.formatText("&a&l--------- LOTTERY ---------"));
                    p.sendMessage("");
                }

                joinedPlayers.clear();
            }
        }, 20 * 60, Integer.MAX_VALUE /* This is honestly overkill. Won't run for another 3.4 years lol */);
    }
}
