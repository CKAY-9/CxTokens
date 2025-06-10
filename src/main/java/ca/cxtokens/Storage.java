package ca.cxtokens;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import ca.cxtokens.Shop.Static.Item;

import java.io.File;
import java.io.IOException;

public class Storage {
    public static File dataFile;
    public static YamlConfiguration data;
    public static File configFile;
    public static YamlConfiguration config;
    public static File storeItemsFile;
    public static YamlConfiguration storeItems;

    public static void initializeData() {
        try {
            // Data (players)
            dataFile = new File(Utils.getPlugin().getDataFolder(), "data.yml");
            if (!dataFile.exists()) {
                if (dataFile.getParentFile().mkdirs()) {
                    Utils.getPlugin().getLogger().info("Created CxTokens folder!");
                }
                if (dataFile.createNewFile()) {
                    Utils.getPlugin().getLogger().info("Created data file!");
                }
            }
            data = YamlConfiguration.loadConfiguration(dataFile);
            data.save(dataFile);

            // Config
            configFile = new File(Utils.getPlugin().getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                if (configFile.getParentFile().mkdirs()) {
                    Utils.getPlugin().getLogger().info("Created CxTokens folder");
                }
                if (configFile.createNewFile()) {
                    Utils.getPlugin().getLogger().info("Created config file");
                }
            }
            config = YamlConfiguration.loadConfiguration(configFile);

            // Fill config
            if (!config.isSet("config.defaultTokenAmount")) {
                config.set("config.defaultTokenAmount", 500L);
            }

            if (!config.isSet("config.currency")) {
                config.set("config.currency", "T$");
            }

            CxTokens.currency = config.getString("config.currency");

            if (!config.isSet("config.useOnlinePlayersOnlyForBalTop")) {
                config.set("config.useOnlinePlayersOnlyForBalTop", true);
            }

            // Mob Rewards
            if (!config.isSet("mobRewards.enabled")) {
                config.set("mobRewards.enabled", true);
                config.set("mobRewards.maxReward", 10L);
                config.set("mobRewards.minReward", 5L);
            }

            if (!config.isSet("mobRewards.custom")) {
                config.set("mobRewards.custom.Zombie.maxReward", 50L);
                config.set("mobRewards.custom.Zombie.minReward", 25L);
            }

            // Routine tokens
            if (!config.isSet("routineTokens.enabled")) {
                config.set("routineTokens.enabled", true);
                config.set("routineTokens.waitTimeInSeconds", 300);
                config.set("routineTokens.amountOfTokens", 50L);
            }

            // Lottery
            if (!config.isSet("lottery.enabled")) {
                config.set("lottery.enabled", true);
                config.set("lottery.waitTimeInSeconds", 1800);
                config.set("lottery.entryCost", 150L);
            }

            // Vaults
            if (!config.isSet("vaults.enabled")) {
                config.set("vaults.enabled", true);
                config.set("vaults.max_value", Long.MAX_VALUE);
                config.set("vaults.cost", 0);
                config.set("vaults.maximum_deposit", 2500);
                config.set("vaults.minimum_deposit", 1);
                config.set("vaults.alert_on_break", true);
            }

            // Auction House
            if (!config.isSet("auction.enabled")) {
                config.set("auction.enabled", true);
                config.set("auction.sweepsPerItem", 60L);
                config.set("auction.bidIncreaseMultiplier", 1.25);
            }

            // Bounty
            if (!config.isSet("bounty.enabled")) {
                config.set("bounty.enabled", true);
                config.set("bounty.showInName", true);
                config.set("bounty.allowSelfBounty", true);
                config.set("bounty.minBounty", 500L);
                config.set("bounty.maxBounty", Long.MAX_VALUE);
            }

            if (!config.isSet("bounty.allow_stacking")) {
                config.set("bounty.allow_stacking", true);
            }

            // Achievements
            if (!config.isSet("achievements.enabled")) {
                config.set("achievements.enabled", true);
                config.set("achievements.disableRecipes", true);
                config.set("achievements.minReward", 50L);
                config.set("achievements.maxReward", 750L);
            }

            if (!config.isSet("achievements.custom")) {
                config.set("achievements.custom.story/enter_the_end.minReward", 1000L);
                config.set("achievements.custom.story/enter_the_end.maxReward", 2000L);
            }

            if (!config.isSet("chunk_selling.enabled")) {
                config.set("chunk_selling.enabled", true);
                config.set("chunk_selling.cooldown_in_minutes", 30);
            }

            if (!config.isSet("static_store.display_item_count")) {
                config.set("static_store.display_item_count", true);
            }

            if (!config.isSet("static_store.enabled")) {
                config.set("static_store.enabled", true);
            }

            if (!config.isSet("player_death.steal_token_percentage")) {
                config.set("player_death.steal_token_percentage", 33);
            }

            config.save(configFile);

            // Static Store Items
            storeItemsFile = new File(Utils.getPlugin().getDataFolder(), "storeItems.yml");
            if (!storeItemsFile.exists()) {
                if (storeItemsFile.getParentFile().mkdirs()) {
                    Utils.getPlugin().getLogger().info("Created CxTokens folder");
                }
                if (storeItemsFile.createNewFile()) {
                    Utils.getPlugin().getLogger().info("Created store items file");
                }
            }
            storeItems = YamlConfiguration.loadConfiguration(storeItemsFile);

            // Default Item Values
            if (!storeItems.isSet("items")) {
                Item exampleCustom = new Item(new ItemStack(Material.IRON_SWORD), 50000, 0);
                storeItems.set("items.customItemWithEnchantAndName.material",
                        exampleCustom.stack.getType().getKey().toString());
                storeItems.set("items.customItemWithEnchantAndName.amount", exampleCustom.stack.getAmount());
                storeItems.set("items.customItemWithEnchantAndName.price", exampleCustom.price);
                storeItems.set("items.customItemWithEnchantAndName.sellMultiplier", exampleCustom.sellMultiplier);
                storeItems.set("items.customItemWithEnchantAndName.enchants.sharpness.enchant", "minecraft:sharpness");
                storeItems.set("items.customItemWithEnchantAndName.enchants.sharpness.level", 5);
                storeItems.set("items.customItemWithEnchantAndName.customName", "THE SHARP SWORD");

                Item[] items = new Item[] {
                        new Item(new ItemStack(Material.OAK_LOG, 16), 100, 0.5),
                        new Item(new ItemStack(Material.OAK_SAPLING, 4), 100, 0.25),
                        new Item(new ItemStack(Material.BIRCH_LOG, 16), 100, 0.5),
                        new Item(new ItemStack(Material.BIRCH_SAPLING, 4), 100, 0.25),
                        new Item(new ItemStack(Material.SPRUCE_LOG, 16), 100, 0.5),
                        new Item(new ItemStack(Material.SPRUCE_SAPLING, 4), 100, 0.25),
                        new Item(new ItemStack(Material.JUNGLE_LOG, 16), 100, 0.5),
                        new Item(new ItemStack(Material.JUNGLE_SAPLING, 4), 100, 0.25),
                        new Item(new ItemStack(Material.DARK_OAK_LOG, 16), 100, 0.5),
                        new Item(new ItemStack(Material.DARK_OAK_SAPLING, 4), 100, 0.25),
                        new Item(new ItemStack(Material.ACACIA_LOG, 16), 100, 0.5),
                        new Item(new ItemStack(Material.ACACIA_SAPLING, 4), 100, 0.25),
                        new Item(new ItemStack(Material.MANGROVE_LOG, 16), 125, 0.5),
                        new Item(new ItemStack(Material.WARPED_STEM, 16), 150, 0.5),
                        new Item(new ItemStack(Material.CRIMSON_STEM, 16), 150, 0.5),
                        new Item(new ItemStack(Material.DIRT, 64), 150, 0.25),
                        new Item(new ItemStack(Material.GRAVEL, 64), 125, 0.25),
                        new Item(new ItemStack(Material.SAND, 64), 125, 0.2),
                        new Item(new ItemStack(Material.ANDESITE, 32), 150, 0.25),
                        new Item(new ItemStack(Material.GRANITE, 32), 150, 0.25),
                        new Item(new ItemStack(Material.COBBLESTONE, 32), 150, 0.25),
                        new Item(new ItemStack(Material.STONE, 32), 200, 0.5),
                        new Item(new ItemStack(Material.SMOOTH_STONE, 8), 200, 0.5),
                        new Item(new ItemStack(Material.GRASS_BLOCK, 8), 150, 0.5),
                        new Item(new ItemStack(Material.COOKED_BEEF, 8), 150, 0.35),
                        new Item(new ItemStack(Material.COOKED_CHICKEN, 8), 140, 0.35),
                        new Item(new ItemStack(Material.COOKED_COD, 16), 130, 0.35),
                        new Item(new ItemStack(Material.COOKED_MUTTON, 16), 140, 0.35),
                        new Item(new ItemStack(Material.COOKED_PORKCHOP, 16), 150, 0.35),
                        new Item(new ItemStack(Material.CAKE, 1), 80, 0.5),
                        new Item(new ItemStack(Material.COOKED_PORKCHOP, 16), 150, 0.35),
                        new Item(new ItemStack(Material.MELON, 16), 120, 0.25),
                        new Item(new ItemStack(Material.BREAD, 16), 130, 0.35),
                        new Item(new ItemStack(Material.COOKED_RABBIT, 16), 140, 0.35),
                        new Item(new ItemStack(Material.BREAD, 16), 140, 0.35),
                        new Item(new ItemStack(Material.COPPER_INGOT, 16), 250, 0.5),
                        new Item(new ItemStack(Material.COAL, 16), 300, 0.5),
                        new Item(new ItemStack(Material.IRON_INGOT, 16), 400, 0.5),
                        new Item(new ItemStack(Material.EMERALD, 4), 400, 0.5),
                        new Item(new ItemStack(Material.DIAMOND, 16), 800, 0.5),
                        new Item(new ItemStack(Material.REDSTONE, 32), 400, 0.5),
                        new Item(new ItemStack(Material.LAPIS_LAZULI, 32), 400, 0.25),
                        new Item(new ItemStack(Material.NETHERITE_INGOT, 16), 1200, 0.5),
                        new Item(new ItemStack(Material.QUARTZ, 16), 250, 0.35),
                        new Item(new ItemStack(Material.AMETHYST_SHARD, 16), 300, 0.35),
                        new Item(new ItemStack(Material.STICK, 64), 350, 0.25),
                        new Item(new ItemStack(Material.FLINT, 8), 125, 0.25),
                        new Item(new ItemStack(Material.BONE, 16), 250, 0.25),
                        new Item(new ItemStack(Material.STRING, 16), 250, 0.25),
                        new Item(new ItemStack(Material.SPIDER_EYE, 16), 250, 0.25),
                        new Item(new ItemStack(Material.ROTTEN_FLESH, 16), 250, 0.25),
                        new Item(new ItemStack(Material.FEATHER, 16), 250, 0.25),
                        new Item(new ItemStack(Material.LEATHER, 16), 450, 0.35),
                        new Item(new ItemStack(Material.BLAZE_ROD, 4), 450, 0.25),
                        new Item(new ItemStack(Material.SLIME_BALL, 8), 400, 0.45),
                        new Item(new ItemStack(Material.ENDER_PEARL, 8), 550, 0.45),
                        new Item(new ItemStack(Material.SHULKER_SHELL, 4), 750, 0.5),
                };

                for (int i = 0; i < items.length; i++) {
                    Item temp = items[i];
                    storeItems.set("items." + i + ".material", temp.stack.getType().getKey().toString());
                    storeItems.set("items." + i + ".amount", temp.stack.getAmount());
                    storeItems.set("items." + i + ".price", temp.price);
                    storeItems.set("items." + i + ".sellMultiplier", temp.sellMultiplier);
                }

                Utils.getPlugin().getLogger()
                        .info("\n\n\n!!! I RECOMMEND CHANGING THE DEFAULT STORE ITEMS CONFIG !!!\n\n");
            }

            storeItems.save(storeItemsFile);
        } catch (IOException ex) {
            Utils.getPlugin().getLogger().warning(ex.toString());
        }
    }
}
