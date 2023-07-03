package ca.camerxn.cxtokens;

import org.bukkit.plugin.java.JavaPlugin;

import ca.camerxn.cxtokens.Commands.BalanceCommand;
import ca.camerxn.cxtokens.Commands.LotteryCommand;
import ca.camerxn.cxtokens.Commands.StoreCommand;
import ca.camerxn.cxtokens.Events.MiscEvents;
import ca.camerxn.cxtokens.Listeners.EntityKill;
import ca.camerxn.cxtokens.Listeners.PlayerJoin;
import ca.camerxn.cxtokens.Listeners.PlayerLeave;
import ca.camerxn.cxtokens.Shop.PurchaseHandle;

public final class CxTokens extends JavaPlugin {

    public Utils utils = new Utils(this);
    public MiscEvents events;

    @Override
    public void onEnable() {
        Config.initializeData();   

        // Events
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        this.getServer().getPluginManager().registerEvents(new EntityKill(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerLeave(this), this);

        // Store handler
        this.getServer().getPluginManager().registerEvents(new PurchaseHandle(), this);
        
        // Commands
        this.getCommand("tlottery").setExecutor(new LotteryCommand(this));
        this.getCommand("tbal").setExecutor(new BalanceCommand());
        this.getCommand("tstore").setExecutor(new StoreCommand());

        // CxToken specific events
        events = new MiscEvents(this);
    }

    @Override
    public void onDisable() {
        events.lottery.running = false;
        events.lottery.joinedPlayers.clear();
    }
}
