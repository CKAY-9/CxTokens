package ca.cxtokens.Shop.Static;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;
import ca.cxtokens.Shop.GlobalShop;

public class StaticInteractionHandle implements Listener {
    /*
     * Double chests have 54 slots (D.Bs are used for the store)
     * 45 = bottom left slot
     * 53 = bottom right slot
     */
    private final int BACK_EXIT = 45;
    private final int NEXT_PAGE = 53;

    private void purchaseItem(Item item, String key, Player player) {
        // Prevent purchases if the blaming inventory is full
        Inventory player_inventory = player.getInventory();
        if (player_inventory.firstEmpty() == -1) {
            // Check all items in inventory
            boolean flag = false;
            for (int i = 0; i < player_inventory.getContents().length; i++) {
                ItemStack temp_stack = player_inventory.getContents()[i];
                if (temp_stack.getType() != item.stack.getType()) continue;

                // Check if adding will go above max size
                Utils.getPlugin().getLogger().info("Item: " + item.stack.getAmount() + ", Stack: " + temp_stack.getAmount() + ", Max: " + item.stack.getMaxStackSize());
                if (item.stack.getAmount() + temp_stack.getAmount() <= item.stack.getMaxStackSize()) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                player.sendMessage(Utils.formatText("&cYou need to have free inventory space to purchase an item"));
                return;
            }
        }

        TokenPlayer tokenPlayer = TokenPlayer.convertPlayerToTokenPlayer(player);
        if (tokenPlayer.getTokens() < item.price) {
            player.sendMessage(Utils.formatText("&cYou don't have enough tokens to purchase this item!"));
            return;
        }

        ItemStack itemStackToGive = new ItemStack(item.stack.getType(), item.stack.getAmount());
        if (Storage.storeItems.isSet("items." + key + ".enchants")) {
            itemStackToGive = item.addEnchantments(key, itemStackToGive);
        }
        if (Storage.storeItems.isSet("items." + key + ".customName")) {
            itemStackToGive = item.setCustomName(key, itemStackToGive);
        }

        tokenPlayer.subtractTokens(item.price, false);
        player_inventory.addItem(itemStackToGive);
    }

    // I honestly can't come up with a better solution rn
    // Will probably make better later
    private boolean canRemoveItemsOnSell(Player player, Item item) {
        ArrayList<Integer> foundIndexes = new ArrayList<>();
        int currentStackAmount = 0;
        int itemNum = item.stack.getAmount();
        int i = 0;

        for (ItemStack it : player.getInventory()) {
            if (it != null && it.getType() == item.stack.getType()) {
                foundIndexes.add(i);
                currentStackAmount += it.getAmount();
                if (currentStackAmount >= itemNum)
                    break;
            }
            i++;
        }

        if (currentStackAmount >= item.stack.getAmount()) {
            for (int j = 0; j < foundIndexes.size(); j++) {
                int index = foundIndexes.get(j);
                itemNum -= player.getInventory().getItem(index).getAmount();
                if (itemNum <= 0) {
                    player.getInventory().getItem(index).setAmount(Math.abs(itemNum));
                } else {
                    player.getInventory().getItem(index).setAmount(0);
                }
            }
            return true;
        }

        return false;
    }

    private void sellItem(Item item, Player player) {
        TokenPlayer tokenPlayer = TokenPlayer.convertPlayerToTokenPlayer(player);
        if (!player.getInventory().contains(item.stack.getType())) {
            player.sendMessage(Utils.formatText("&cYou have to have this item in your inventory to sell!"));
            return;
        }

        if (!canRemoveItemsOnSell(player, item)) {
            player.sendMessage(Utils.formatText("&cYou don't have enough of this item to sell it!"));
            return;
        }

        long sellPrice = Math.round(item.price * item.sellMultiplier);
        tokenPlayer.addTokens(sellPrice, false);
    }

    @EventHandler
    public void onStoreClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) {
            return;
        }
        if (!e.getView().getTitle().contains("Store")) {
            return;
        } else {
            e.setCancelled(true);
        }
        if (e.getClickedInventory().getHolder() != null) {
            return;
        }
        if (e.getClickedInventory().getType() != InventoryType.CHEST) {
            return;
        }

        int currentPage = Integer.parseInt(e.getView().getTitle().split(" ")[3]) - 1;
        int clicked = e.getSlot();

        if (clicked == BACK_EXIT) {
            e.getView().close();

            // Exit
            if (currentPage == 0) {
                return;
            }

            // Go Back
            Store.openStaticStorePage(TokenPlayer.convertPlayerToTokenPlayer((Player) e.getWhoClicked()),
                    (currentPage - 1));
            return;
        }

        if (clicked == NEXT_PAGE && currentPage < Math.round(Storage.storeItems.getConfigurationSection("items").getKeys(false).size() / GlobalShop.MAX_ITEMS_PER_PAGE)) {
            e.getView().close();
            Store.openStaticStorePage(TokenPlayer.convertPlayerToTokenPlayer((Player) e.getWhoClicked()),
                    (currentPage + 1));
            return;
        }

        Set<String> storeItemKeys = Storage.storeItems.getConfigurationSection("items").getKeys(false);
        int index = clicked + (GlobalShop.MAX_ITEMS_PER_PAGE * currentPage);

        if (clicked >= GlobalShop.MAX_ITEMS_PER_PAGE || clicked > (storeItemKeys.size() - (GlobalShop.MAX_ITEMS_PER_PAGE * currentPage) - 1)) {
            return;
        }

        String key = (String) storeItemKeys.toArray()[index];
        Item item = new Item(
            new ItemStack(
                Material.matchMaterial(Storage.storeItems.getString("items." + key + ".material", "air")),
                Storage.storeItems.getInt("items." + key + ".amount", 0)),
            Storage.storeItems.getLong("items." + key + ".price", 0),
            Storage.storeItems.getDouble("items." + key + ".sellMultiplier", 0)
        );

        if (e.getClick().isLeftClick()) {
            purchaseItem(item, key, (Player) e.getWhoClicked());
            return;
        }
        sellItem(item, (Player) e.getWhoClicked());
    }
}