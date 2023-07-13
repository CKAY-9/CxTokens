package ca.cxtokens.Shop.Auction;

import org.bukkit.entity.Player;

public class Viewer {
    public Player player;
    public int page;
    private AuctionHouse house;

    public Viewer(Player player, int page, AuctionHouse house) {
        this.player = player;
        this.page = page;
        this.house = house;
    }

    public void openPage(int page) {
        this.page = page;
        this.house.openAuctionHouse(this.player, this.page);
    }

    public void nextPage() {
        this.house.openAuctionHouse(this.player, this.page + 1);
        this.page = page + 1;
    }

    public void prevPage() {
        this.house.openAuctionHouse(this.player, this.page - 1);
        this.page = page - 1;
    }
}
