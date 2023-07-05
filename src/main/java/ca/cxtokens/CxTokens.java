package ca.cxtokens;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import ca.cxtokens.Commands.AboutCommand;
import ca.cxtokens.Commands.AuctionCommand;
import ca.cxtokens.Commands.BalTopCommand;
import ca.cxtokens.Commands.BalanceCommand;
import ca.cxtokens.Commands.BountyCommand;
import ca.cxtokens.Commands.LotteryCommand;
import ca.cxtokens.Commands.SendCommand;
import ca.cxtokens.Commands.StoreCommand;
import ca.cxtokens.Commands.Completers.AuctionCompleter;
import ca.cxtokens.Commands.Completers.BountyCompleter;
import ca.cxtokens.Commands.Completers.SendCompleter;
import ca.cxtokens.Events.MiscEvents;
import ca.cxtokens.Listeners.EntityKill;
import ca.cxtokens.Listeners.PlayerDeath;
import ca.cxtokens.Listeners.PlayerJoin;
import ca.cxtokens.Listeners.PlayerLeave;
import ca.cxtokens.Shop.Auction.AuctionHouse;
import ca.cxtokens.Shop.Auction.AuctionInteractionHandle;
import ca.cxtokens.Shop.Static.StaticInteractionHandle;

public final class CxTokens extends JavaPlugin {

    public Utils utils = new Utils(this);
    public MiscEvents events;
    public AuctionHouse auctionHouse;

    @Override
    public void onEnable() {
        Storage.initializeData();   

        // Events
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        this.getServer().getPluginManager().registerEvents(new EntityKill(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerLeave(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDeath(), this);

        // Store handlers
        this.getServer().getPluginManager().registerEvents(new StaticInteractionHandle(), this);
        this.getServer().getPluginManager().registerEvents(new AuctionInteractionHandle(this), this);

        // Commands
        this.getCommand("tlottery").setExecutor(new LotteryCommand(this));
        this.getCommand("tbal").setExecutor(new BalanceCommand());
        this.getCommand("tstore").setExecutor(new StoreCommand());
        this.getCommand("tsend").setExecutor(new SendCommand());
        this.getCommand("ttop").setExecutor(new BalTopCommand());
        this.getCommand("tbounty").setExecutor(new BountyCommand());
        this.getCommand("tauction").setExecutor(new AuctionCommand(this));
        this.getCommand("cxtokens").setExecutor(new AboutCommand());

        // Completers
        this.getCommand("tauction").setTabCompleter(new AuctionCompleter());
        this.getCommand("tsend").setTabCompleter(new SendCompleter());
        this.getCommand("tbounty").setTabCompleter(new BountyCompleter());

        // CxToken specific events
        events = new MiscEvents(this);
        auctionHouse = new AuctionHouse(this);

        // this is used if the plugin is reset and setup values for every player
        for (Player p : Bukkit.getOnlinePlayers()) {
            TokenPlayer.convertPlayerToTokenPlayer(p);
        }
    }

    @Override
    public void onDisable() {
        events.lottery.running = false;
        events.lottery.joinedPlayers.clear();
    }
}
