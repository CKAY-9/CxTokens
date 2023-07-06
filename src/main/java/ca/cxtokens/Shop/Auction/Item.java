package ca.cxtokens.Shop.Auction;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ca.cxtokens.Storage;

public class Item {
    public Player seller;
    public Player bidder;
    public long currentBid;
    public ItemStack item;
    public boolean sold;
    public long sweepsUntilComplete;

    public Item(Player seller, long currentBid, ItemStack item) {
        this.seller = seller;
        this.currentBid = currentBid;
        this.item = item;
        this.sold = false;
        this.sweepsUntilComplete = Storage.config.getLong("auction.sweepsPerItem", 60);
        this.bidder = null;
    }
}
