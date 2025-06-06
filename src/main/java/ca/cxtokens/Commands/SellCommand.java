package ca.cxtokens.Commands;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

class ItemData {
    int amount;
    long price;
    double sell_multiplier;

    public ItemData(int amount, long price, double sell_multiplier) {
        this.price = price;
        this.amount = amount;
        this.sell_multiplier = sell_multiplier;
    }
}

public class SellCommand implements CommandExecutor {
    private HashMap<UUID, Integer> chunk_cooldowns;
    private CxTokens tokens;

    public SellCommand(CxTokens tokens) {
        this.tokens = tokens;
    }

    public SellCommand() {
        this.chunk_cooldowns = new HashMap<>();
        if (Storage.config.getBoolean("chunk_selling.enabled", true)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.chunk_cooldowns.put(player.getUniqueId(), 0);
            }

            Utils.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(Utils.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        UUID uuid = player.getUniqueId();
                        Integer cooldown = chunk_cooldowns.get(uuid);
                        if (cooldown == null) {
                            chunk_cooldowns.put(uuid, 0);
                            cooldown = 0;
                        }

                        if (cooldown > 0) {
                            chunk_cooldowns.put(uuid, cooldown - 1);
                        }
                    }
                }
            }, 0, 20 * 60);
        }
    }

    private HashMap<String, ItemData> loadStaticStoreItems() {
        HashMap<String, ItemData> map = new HashMap<>();
        Set<String> keys = Storage.storeItems.getConfigurationSection("items").getKeys(false);
        String[] key_array = keys.toArray(String[]::new);
        for (int i = 0; i < keys.size(); i++) {
            String key = key_array[i];
            String material = Storage.storeItems.getString("items." + key + ".material", "air");
            int amount = Storage.storeItems.getInt("items." + key + ".amount", 0);
            long price = Storage.storeItems.getLong("items." + key + ".price", 0);
            double sell_multiplier = Storage.storeItems.getDouble("items." + key + ".sellMultiplier", 0);

            map.put(material.toLowerCase(), new ItemData(amount, price, sell_multiplier));
        }

        return map;
    }

    public long sellContainer(TokenPlayer token_player, Container container) {
        Inventory inventory = container.getInventory();
        ItemStack[] stacks = inventory.getContents();
        HashMap<String, ItemData> static_items = loadStaticStoreItems();
        HashMap<String, Integer> items = new HashMap<>();
        for (int i = 0; i < stacks.length; i++) {
            ItemStack stack = stacks[i];
            if (stack == null) {
                continue;
            }

            String material = "minecraft:" + stack.getType().toString().toLowerCase();
            ItemData store_item = static_items.get(material);
            if (store_item == null) {
                continue;
            }

            if (items.get(material) == null) {
                items.put(material, stack.getAmount());
            } else {
                items.put(material, items.get(material) + stack.getAmount());
            }

            stack.setAmount(0);
        }

        long total_value = 0;
        for (String key : items.keySet()) {
            int val = items.get(key);
            ItemData store_item = static_items.get(key);
            if (val < store_item.amount) {
                continue;
            }

            int stack_total = val;
            long sell = Math.round(store_item.price * store_item.sell_multiplier);
            while (stack_total >= store_item.amount) {
                stack_total -= store_item.amount;
                total_value += sell;
                token_player.addTokens(sell, true);
            }

            if (stack_total >= 1) {
                inventory.addItem(new ItemStack(Material.matchMaterial(key), stack_total));
            }
        }

        return total_value;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.formatText("&cMust be a player to use this command!"));
            return false;
        }

        Player player = (Player) sender;
        TokenPlayer token_player = TokenPlayer.getTokenPlayer(this.tokens, player);
        
        if (args.length == 1 && args[0].equalsIgnoreCase("chunk")) {
            Integer cooldown = this.chunk_cooldowns.get(player.getUniqueId());
            if (cooldown != null && cooldown > 0) {
                player.sendMessage(Utils.formatText("&cChunk selling is on cooldown for &c&l" + cooldown + "m"));
                return false;
            }
            
            Chunk player_chunk = player.getLocation().getChunk();
            World player_world = player.getWorld();
            long total_value = 0;
            
            for (int y = player_world.getMinHeight(); y < player_world.getMaxHeight(); y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        Block block = player_chunk.getBlock(x, y, z);
                        BlockState state = block.getState();
                        if (block == null || !(state instanceof Container)) {
                            continue;
                        }

                        Container container = (Container) state;
                        total_value += sellContainer(token_player, container);
                    }
                }
            }

            if (total_value > 0) {
                player.sendMessage(Utils.formatText("&aSold chunk for &a&l" + CxTokens.currency + "" + total_value));
            }
            this.chunk_cooldowns.put(player.getUniqueId(), Storage.config.getInt("chunk_selling.cooldown_in_minutes", 30));

            return false;
        }

        int distance_limit = 5;
        Block target_block = player.getTargetBlockExact(distance_limit);
        if (target_block == null) {
            player.sendMessage(Utils.formatText("&cMust be looking at a container to use this command!"));
            return false;
        }

        BlockState state = target_block.getState();
        if (!(state instanceof Container)) {
            player.sendMessage(Utils.formatText("&cMust be looking at a container to use this command!"));
            return false;
        }

        Container container = (Container) state;
        long value = sellContainer(token_player, container);
        if (value > 0) {
            player.sendMessage(Utils.formatText("&aSold container for &a&l" + CxTokens.currency + "" + value));
        }
        return false;
    }

}
