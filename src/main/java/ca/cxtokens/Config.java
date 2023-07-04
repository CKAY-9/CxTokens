package ca.cxtokens;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    public static File dataFile;
    public static YamlConfiguration data;
    public static File configFile;
    public static YamlConfiguration config;

    public static void initializeData() {
        try {
            // Data (players)
            dataFile = new File(Utils.getPlugin().getDataFolder(), "data.yml");
            if (!dataFile.exists()) {
                if (dataFile.getParentFile().mkdirs()) {
                    Utils.getPlugin().getLogger().info("Created CxTokens folder!");
                }
                if (dataFile.createNewFile()) {
                    Utils.getPlugin().getLogger().info("Created data file!");
                }
            }
            data = YamlConfiguration.loadConfiguration(dataFile);
            data.save(dataFile);

            // Config
            configFile = new File(Utils.getPlugin().getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                if (configFile.getParentFile().mkdirs()) {
                    Utils.getPlugin().getLogger().info("Created CxTokens folder");
                }
                if (configFile.createNewFile()) {
                    Utils.getPlugin().getLogger().info("Created config file");
                } 
            }
            config = YamlConfiguration.loadConfiguration(configFile);

            // Fill config
            if (!config.isSet("config.defaultTokenAmount")) {
                config.set("config.defaultTokenAmount", 500);
            }
            
            // Mob Rewards
            if (!config.isSet("mobRewards.enabled")) {
                config.set("mobRewards.enabled", true);
                config.set("mobRewards.maxReward", 10);
                config.set("mobRewards.minReward", 5);
            }

            // Routine tokens
            if (!config.isSet("routineTokens.enabled")) {
                config.set("routineTokens.enabled", true);
                config.set("routineTokens.waitTimeInSeconds", 300);
                config.set("routineTokens.amountOfTokens", 50);
            }

            // Lottery
            if (!config.isSet("lottery.enabled")) {
                config.set("lottery.enabled", true);
                config.set("lottery.waitTimeInSeconds", 1800);
                config.set("lottery.entryCost", 150);
            }

            // Auction House
            if (!config.isSet("auction.enabled")) {
                config.set("auction.enabled", true);
                config.set("auction.sweepsPerItem", 60);
                config.set("auction.bidIncreaseMultiplier", 1.25);
            }

            config.save(configFile);
        } catch (IOException ex) {
            Utils.getPlugin().getLogger().warning(ex.toString());
        }
    }
}
