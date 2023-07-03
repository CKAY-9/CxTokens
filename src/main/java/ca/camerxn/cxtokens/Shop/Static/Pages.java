package ca.camerxn.cxtokens.Shop.Static;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Pages {
    // Pages are limited to 36 items
    public static Item[] pageOne = new Item[]{
        new Item(new ItemStack(Material.OAK_LOG, 16), 100, 0.5f),
        new Item(new ItemStack(Material.BIRCH_LOG, 16), 100, 0.5f),
        new Item(new ItemStack(Material.SPRUCE_LOG, 16), 100, 0.5f),
        new Item(new ItemStack(Material.JUNGLE_LOG, 16), 100, 0.5f),
        new Item(new ItemStack(Material.DARK_OAK_LOG, 16), 100, 0.5f),
    };

    public static Item[][] pages = {
        pageOne
    };
} 