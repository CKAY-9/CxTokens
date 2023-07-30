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

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static boolean isBool(String s) {
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false");
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
