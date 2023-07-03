package ca.camerxn.cxtokens.Shop.Static;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import ca.camerxn.cxtokens.TokenPlayer;

public class Store {
    public Inventory storeInv;
    public TokenPlayer player;

    public Store(TokenPlayer player) {
        this.player = player;

        this.storeInv = Bukkit.createInventory(null, 54, "Store");

        fillStore(0);
    }

    public void fillStore(int pageIndex) {
        this.storeInv.clear();

        Item[] items = Pages.pages[pageIndex];
        for (int i = 0; i < items.length; i++) {
            storeInv.setItem(i, items[i].stack);
        }

        this.storeInv.
        this.player.ply.openInventory(this.storeInv);
    }
}
