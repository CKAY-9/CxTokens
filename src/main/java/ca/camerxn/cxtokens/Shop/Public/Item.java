package ca.camerxn.cxtokens.Shop.Public;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ca.camerxn.cxtokens.Config;

public class Item {
    public Player seller;
    public Player bidder;
    public int currentBid;
    public ItemStack item;
    public int sweepsUntilComplete;

    public Item(Player seller, int currentBid, ItemStack item) {
        this.seller = seller;
        this.currentBid = currentBid;
        this.item = item;
        this.sweepsUntilComplete = Config.config.getInt("auction.sweepsPerItem", 60);
        this.bidder = null;
    }
}
