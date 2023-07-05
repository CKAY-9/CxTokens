package ca.cxtokens.Shop.Static;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

public class Store {
    /*
     * Static store has hardcoded values and can't be changed in-game.
     * This is different from the Auction House which users can put items in and
     * auction on them.
     */

    public static void openStaticStorePage(TokenPlayer player, int pageIndex) {
        Inventory storeInv = Bukkit.createInventory(null, 54, Utils.formatText("&a&lStore - Page " + (pageIndex + 1)));
        storeInv.clear();

        // Fill enchant books
        

        Item[] items = Pages.pages[pageIndex];
        for (int i = 0; i < items.length; i++) {
            ItemStack stack = new ItemStack(items[i].stack.getType(), 1);
            ItemMeta meta = stack.getItemMeta();

            ArrayList<String> lore = new ArrayList<>();
            lore.add(Utils.formatText("&aBUY " + items[i].stack.getAmount() + " FOR T$" + items[i].price));
            lore.add(Utils.formatText("&cSELL " + items[i].stack.getAmount() + " FOR T$" + Math.round(items[i].price * items[i].sellMultiplier)));
            meta.setLore(lore);

            stack.setItemMeta(meta);

            storeInv.setItem(i, stack);
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

        if (pageIndex < Pages.pages.length - 1) {
            ItemStack next = new ItemStack(Material.GREEN_CONCRETE, 1);
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.setDisplayName(Utils.formatText("&aGo to page " + (pageIndex + 2))); // + 2 is just so the pages go 1, 2, 3,... instead of 0, 1, 2,...
            next.setItemMeta(nextMeta);
            storeInv.setItem(53, next);
        }
        
        player.ply.openInventory(storeInv);
    }
}
