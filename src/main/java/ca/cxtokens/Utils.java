package ca.cxtokens;

import org.bukkit.*;
import org.bukkit.plugin.Plugin;

public class Utils {

    public CxTokens tokens;
    public Utils(CxTokens tokens) {
        this.tokens = tokens;
    }

    public static String formatText(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String adminMessage(String s) {
        return ChatColor.translateAlternateColorCodes('&', "&6&l[CxTokens]&r " + s);
    }

    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("CxTokens");
    }
}
