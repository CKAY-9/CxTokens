package ca.camerxn.cxtokens.Shop.Static;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ca.camerxn.cxtokens.TokenPlayer;
import ca.camerxn.cxtokens.Utils;

public class Store {
    public Inventory storeInv;
    public TokenPlayer player;
    public int page = 0;

    public Store(TokenPlayer player, int page) {
        this.page = page;
        this.player = player;
        fillStore(page);
    }

    public void fillStore(int pageIndex) {
        this.storeInv = Bukkit.createInventory(null, 54, "Store - Page " + (page + 1));
        this.storeInv.clear();

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
        if (page == 0) {
            boeMeta.setDisplayName(Utils.formatText("&cExit"));
        } else {
            boeMeta.setDisplayName(Utils.formatText("&cBack to page " + page--));
        }
        backOrExit.setItemMeta(boeMeta);
        storeInv.setItem(45, backOrExit);

        if (page < Pages.pages.length - 1) {
            ItemStack next = new ItemStack(Material.GREEN_CONCRETE, 1);
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.setDisplayName(Utils.formatText("&aGo to page " + page++));
            next.setItemMeta(nextMeta);
            storeInv.setItem(53, next);
        }
        
        this.player.ply.openInventory(this.storeInv);
    }
}
