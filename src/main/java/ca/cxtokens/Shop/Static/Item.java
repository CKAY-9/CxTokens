package ca.cxtokens.Shop.Static;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import ca.cxtokens.Storage;
import ca.cxtokens.Utils;

public class Item {
    public ItemStack stack = new ItemStack(Material.AIR, 0);
    public long price = 0L;
    public double sellMultiplier = 0.5;

    public Item(ItemStack stack, long price, double sellMultiplier) {
        this.stack = stack;
        this.price = price;
        this.sellMultiplier = sellMultiplier;
    }

    public ItemStack addEnchantments(String storageKey, ItemStack stack) {
        Set<String> enchantKeys = Storage.storeItems.getConfigurationSection("items." + storageKey + ".enchants").getKeys(false);

        for (String eKey : enchantKeys) {
            String enchantToAdd = Storage.storeItems.getString("items." + storageKey + ".enchants." + eKey + ".enchant", "");
            int enchantLevel = Storage.storeItems.getInt("items." + storageKey + ".enchants." + eKey + ".level", 0);

            Enchantment enchantmentToAdd = Enchantment.getByKey(NamespacedKey.fromString(enchantToAdd));

            if (enchantmentToAdd == null) continue;

            if (this.stack.getType() == Material.ENCHANTED_BOOK) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
                meta.addStoredEnchant(enchantmentToAdd, enchantLevel, true);
                stack.setItemMeta(meta);
            } else {
                stack.addUnsafeEnchantment(enchantmentToAdd, enchantLevel);
            }
        }

        return stack;
    }

    public ItemStack setCustomName(String storageKey, ItemStack stack) {
        String customName = Storage.storeItems.getString("items." + storageKey + ".customName", "");
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Utils.formatText(customName));
        stack.setItemMeta(meta);

        return stack;
    }
}
