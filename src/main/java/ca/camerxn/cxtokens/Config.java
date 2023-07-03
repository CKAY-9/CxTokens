package ca.camerxn.cxtokens;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    public static File dataFile;
    public static YamlConfiguration data;
    public static File leaderboardFile;
    public static YamlConfiguration leaderboard;

    public static void initializeData() {
        try {
            dataFile = new File(Utils.getPlugin().getDataFolder(), "config.yml");
            if (!dataFile.exists()) {
                if (dataFile.getParentFile().mkdirs()) {
                    Utils.getPlugin().getLogger().info("Created data folder!");
                }
                if (dataFile.createNewFile()) {
                    Utils.getPlugin().getLogger().info("Created config file!");
                }
            }
            data = YamlConfiguration.loadConfiguration(dataFile);
        } catch (IOException ex) {
            Utils.getPlugin().getLogger().warning(ex.toString());
        }
    }
}
