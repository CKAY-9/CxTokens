package ca.cxtokens.Shop.Static;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Pages {
    // Pages are limited to 36 items

    public static Item[] basicBlocks = new Item[]{
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
        new Item(new ItemStack(Material.SMOOTH_STONE, 8), 200, 0.5f),
        new Item(new ItemStack(Material.GRASS_BLOCK, 8), 150, 0.5f),
    };

    private static Item[] food = new Item[]{
        new Item(new ItemStack(Material.COOKED_BEEF, 8), 150, 0.35f),
        new Item(new ItemStack(Material.COOKED_CHICKEN, 8), 140, 0.35f),
        new Item(new ItemStack(Material.COOKED_COD, 16), 130, 0.35f),
        new Item(new ItemStack(Material.COOKED_MUTTON, 16), 140, 0.35f),
        new Item(new ItemStack(Material.COOKED_PORKCHOP, 16), 150, 0.35f),
        new Item(new ItemStack(Material.CAKE, 1), 80, 0.5f),
        new Item(new ItemStack(Material.COOKED_PORKCHOP, 16), 150, 0.35f),
    };

    private static Item[] resources = new Item[]{
        new Item(new ItemStack(Material.COPPER_INGOT, 16), 250, 0.5f),
        new Item(new ItemStack(Material.COAL, 16), 300, 0.5f),
        new Item(new ItemStack(Material.IRON_INGOT, 16), 400, 0.5f),
        new Item(new ItemStack(Material.EMERALD, 4), 400, 0.5f),
        new Item(new ItemStack(Material.DIAMOND, 16), 800, 0.5f),
        new Item(new ItemStack(Material.REDSTONE, 32), 400, 0.5f),
        new Item(new ItemStack(Material.LAPIS_LAZULI, 32), 400, 0.5f),
        new Item(new ItemStack(Material.NETHERITE_INGOT, 16), 1200, 0.5f),
    };

    public static Item[][] pages = {
        basicBlocks,
        food,
        resources,
    };
} 