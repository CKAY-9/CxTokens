package ca.cxtokens;

import java.util.Base64;
import java.util.Map.Entry;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
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

    public static String base64Encode(String input) {
        String encoded = Base64.getEncoder().encodeToString(input.getBytes());
        return encoded;
    }

    public static String generateHashFromItemStack(ItemStack stack) {
        StringBuilder builder = new StringBuilder();
        for (Entry<Enchantment, Integer> entry : stack.getEnchantments().entrySet()) {
            builder.append(entry.getKey().getKey().toString() + entry.getValue());
        }

        builder.append(stack.getType().getKey().toString());
        String base64 = base64Encode(builder.toString());
        return base64;
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
