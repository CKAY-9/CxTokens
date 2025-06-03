package ca.cxtokens.Commands;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Material;
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.formatText("&cMust be a player to use this command!"));
            return false;
        }

        Player player = (Player) sender;
        TokenPlayer token_player = TokenPlayer.convertPlayerToTokenPlayer(player);
        int distance_limit = 5;
        Block target_block = player.getTargetBlockExact(distance_limit);
        BlockState state = target_block.getState();
        if (target_block == null || !(state instanceof Container)) {
            player.sendMessage(Utils.formatText("&cMust be looking at a container to use this command!"));
            return false;
        }

        Container container = (Container) state;
        Inventory inventory = container.getInventory();
        ItemStack[] stacks = inventory.getContents();
        HashMap<String, ItemData> static_items = loadStaticStoreItems();
        HashMap<String, Integer> items = new HashMap<>();
        for (int i = 0; i < stacks.length; i++) {
            ItemStack stack = stacks[i];
            if (stack == null) {
                continue;
            }

            String material = "minecraft:" +  stack.getType().toString().toLowerCase();
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

        int total_value = 0;
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

            // TODO: Save enchantments
            if (stack_total >= 1) {
                inventory.addItem(new ItemStack(Material.matchMaterial(key), stack_total));
            }
        }

        player.sendMessage(Utils.formatText("&aSold container for a total of " + CxTokens.currency + "" + total_value));
        return false;
    }
    
}
