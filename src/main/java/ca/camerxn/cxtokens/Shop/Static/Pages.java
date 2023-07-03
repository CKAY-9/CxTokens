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
        new Item(new ItemStack(Material.ACACIA_LOG, 16), 100, 0.5f),
        new Item(new ItemStack(Material.MANGROVE_LOG, 16), 100, 0.5f),
        new Item(new ItemStack(Material.WARPED_STEM, 16), 150, 0.5f),
        new Item(new ItemStack(Material.CRIMSON_STEM, 16), 150, 0.5f),
        new Item(new ItemStack(Material.DIRT, 64), 150, 0.25f),
        new Item(new ItemStack(Material.GRAVEL, 64), 125, 0.25f),
        new Item(new ItemStack(Material.ANDESITE, 32), 150, 0.25f),
        new Item(new ItemStack(Material.GRANITE, 32), 150, 0.25f),
        new Item(new ItemStack(Material.COBBLESTONE, 32), 150, 0.25f),
        new Item(new ItemStack(Material.STONE, 32), 200, 0.5f),
        new Item(new ItemStack(Material.GRASS_BLOCK, 8), 150, 0.5f),
    };

    public static Item[][] pages = {
        pageOne
    };
} 