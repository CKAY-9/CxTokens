package ca.camerxn.cxtokens;

import org.bukkit.plugin.java.JavaPlugin;

import ca.camerxn.cxtokens.Commands.AboutCommand;
import ca.camerxn.cxtokens.Commands.AuctionCommand;
import ca.camerxn.cxtokens.Commands.BalTopCommand;
import ca.camerxn.cxtokens.Commands.BalanceCommand;
import ca.camerxn.cxtokens.Commands.LotteryCommand;
import ca.camerxn.cxtokens.Commands.SendCommand;
import ca.camerxn.cxtokens.Commands.StoreCommand;
import ca.camerxn.cxtokens.Commands.Completers.AuctionCompleter;
import ca.camerxn.cxtokens.Commands.Completers.SendCompleter;
import ca.camerxn.cxtokens.Events.MiscEvents;
import ca.camerxn.cxtokens.Listeners.EntityKill;
import ca.camerxn.cxtokens.Listeners.PlayerJoin;
import ca.camerxn.cxtokens.Listeners.PlayerLeave;
import ca.camerxn.cxtokens.Shop.Public.AuctionHouse;
import ca.camerxn.cxtokens.Shop.Public.AuctionInteractionHandle;
import ca.camerxn.cxtokens.Shop.Static.StaticInteractionHandle;

public final class CxTokens extends JavaPlugin {

    public Utils utils = new Utils(this);
    public MiscEvents events;
    public AuctionHouse auctionHouse;

    @Override
    public void onEnable() {
        Config.initializeData();   

        // Events
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        this.getServer().getPluginManager().registerEvents(new EntityKill(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerLeave(this), this);

        // Store handlers
        this.getServer().getPluginManager().registerEvents(new StaticInteractionHandle(), this);
        this.getServer().getPluginManager().registerEvents(new AuctionInteractionHandle(this), this);

        // Commands
        this.getCommand("tlottery").setExecutor(new LotteryCommand(this));
        this.getCommand("tbal").setExecutor(new BalanceCommand());
        this.getCommand("tstore").setExecutor(new StoreCommand());
        this.getCommand("tsend").setExecutor(new SendCommand());
        this.getCommand("ttop").setExecutor(new BalTopCommand());
        this.getCommand("tauction").setExecutor(new AuctionCommand(this));
        this.getCommand("cxtokens").setExecutor(new AboutCommand());

        // Completers
        this.getCommand("tauction").setTabCompleter(new AuctionCompleter());
        this.getCommand("tsend").setTabCompleter(new SendCompleter());

        // CxToken specific events
        events = new MiscEvents(this);
        auctionHouse = new AuctionHouse(this);
    }

    @Override
    public void onDisable() {
        events.lottery.running = false;
        events.lottery.joinedPlayers.clear();
    }
}
