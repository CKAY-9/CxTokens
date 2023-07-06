package ca.cxtokens.Shop.Static;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;
import ca.cxtokens.Shop.GlobalShop;

public class Store {
    /*
     * Static store has hardcoded values and can't be changed in-game.
     * This is different from the Auction House which users can put items in and
     * auction on them.
     */

    public static void openStaticStorePage(TokenPlayer player, int pageIndex) {
        Inventory storeInv = Bukkit.createInventory(null, 54, Utils.formatText("&a&lStore - Page " + (pageIndex + 1)));
        storeInv.clear();

        // fuckery, same goes in the Auction House
        // this just makes sure we don't over index our array
        Set<String> keys = Storage.storeItems.getConfigurationSection("items").getKeys(false);
        int start = pageIndex * GlobalShop.MAX_ITEMS_PER_PAGE;
        int limit = GlobalShop.MAX_ITEMS_PER_PAGE;
        if (start > 0) {
            limit = (keys.size() % start) + GlobalShop.MAX_ITEMS_PER_PAGE;
        }
        if (keys.size() < GlobalShop.MAX_ITEMS_PER_PAGE) {
            limit = keys.size();
        }

        int storageIndex = 0;
        for (int i = (0 + start); i < limit; i++) {
            if (storageIndex == GlobalShop.MAX_ITEMS_PER_PAGE) {
                break;
            }

            // construct item
            String key = (String) keys.toArray()[i];
            Item temp = new Item(
                new ItemStack(
                    Material.matchMaterial(Storage.storeItems.getString("items." + key + ".material", "air")),
                    Storage.storeItems.getInt("items." + key + ".amount", 0)
                ),
                Storage.storeItems.getLong("items." + key + ".price", 0),
                Storage.storeItems.getDouble("items." + key + ".sellMultiplier", 0)
            );


            ItemStack stack = new ItemStack(temp.stack.getType(), 1);
            ItemMeta meta = stack.getItemMeta();

            ArrayList<String> lore = new ArrayList<>();
            lore.add(Utils.formatText("&aBUY " + temp.stack.getAmount() + " FOR " + CxTokens.currency + temp.price));
            lore.add(Utils.formatText("&cSELL " + temp.stack.getAmount() + " FOR " + CxTokens.currency + Math.round(temp.price * temp.sellMultiplier)));

            meta.setLore(lore);
            stack.setItemMeta(meta);

            if (Storage.storeItems.isSet("items." + key + ".enchants")) {
                stack = temp.addEnchantments(key, stack);
            }
            if (Storage.storeItems.isSet("items." + key + ".customName")) {
                stack = temp.setCustomName(key, stack);
            }

            storeInv.setItem(storageIndex, stack);
            storageIndex++;
        }

        ItemStack backOrExit = new ItemStack(Material.RED_CONCRETE, 1);
        ItemMeta boeMeta = backOrExit.getItemMeta();
        if (pageIndex == 0) {
            boeMeta.setDisplayName(Utils.formatText("&cExit"));
        } else {
            boeMeta.setDisplayName(Utils.formatText("&cBack to page " + (pageIndex)));
        }
        backOrExit.setItemMeta(boeMeta);
        storeInv.setItem(45, backOrExit);

        if (pageIndex < Math.round(keys.size() / GlobalShop.MAX_ITEMS_PER_PAGE)) {
            ItemStack next = new ItemStack(Material.GREEN_CONCRETE, 1);
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.setDisplayName(Utils.formatText("&aGo to page " + (pageIndex + 2))); // + 2 is just so the pages go 1, 2, 3,... instead of 0, 1, 2,...
            next.setItemMeta(nextMeta);
            storeInv.setItem(53, next);
        }
        
        player.ply.openInventory(storeInv);
    }
}
