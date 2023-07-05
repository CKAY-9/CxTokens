package ca.cxtokens.Shop.Static;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Item {
    public ItemStack stack = new ItemStack(Material.AIR, 0);
    public int price = 0;
    public double sellMultiplier = 0.5;

    public Item(ItemStack stack, int price, double sellMultiplier) {
        this.stack = stack;
        this.price = price;
        this.sellMultiplier = sellMultiplier;
    }
}
