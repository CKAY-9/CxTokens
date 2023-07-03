package ca.camerxn.cxtokens.Shop;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class PurchaseHandle implements Listener {
    @EventHandler
    public void onStoreClick(InventoryClickEvent e) {
        if (e.getClickedInventory().getHolder() == null || e.getClickedInventory().getType() == InventoryType.CHEST) {
            e.setCancelled(true);
            e.getWhoClicked().sendMessage("Purchasing item " + e.getSlot());
        }
    }
}
