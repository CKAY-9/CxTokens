package ca.camerxn.cxtokens.Shop.Static;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Item {
    public ItemStack stack = new ItemStack(Material.AIR, 0);
    public int price = 0;
    public float sellMultiplier = 0.5f;

    public Item(ItemStack stack, int price, float sellMultiplier) {
        this.stack = stack;
        this.price = price;
        this.sellMultiplier = sellMultiplier;
    }
}
