package ca.cxtokens;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import ca.cxtokens.Commands.AboutCommand;
import ca.cxtokens.Commands.AdminCommand;
import ca.cxtokens.Commands.AuctionCommand;
import ca.cxtokens.Commands.BalTopCommand;
import ca.cxtokens.Commands.BalanceCommand;
import ca.cxtokens.Commands.BountyCommand;
import ca.cxtokens.Commands.LotteryCommand;
import ca.cxtokens.Commands.ResetCommand;
import ca.cxtokens.Commands.SellCommand;
import ca.cxtokens.Commands.SendCommand;
import ca.cxtokens.Commands.StoreCommand;
import ca.cxtokens.Commands.VaultCommand;
import ca.cxtokens.Commands.Completers.AdminCompleter;
import ca.cxtokens.Commands.Completers.AuctionCompleter;
import ca.cxtokens.Commands.Completers.BountyCompleter;
import ca.cxtokens.Commands.Completers.SellCompleter;
import ca.cxtokens.Commands.Completers.SendCompleter;
import ca.cxtokens.Events.HTTPUpdate;
import ca.cxtokens.Events.MiscEvents;
import ca.cxtokens.Listeners.Achievements;
import ca.cxtokens.Listeners.EntityKill;
import ca.cxtokens.Listeners.PlayerDeath;
import ca.cxtokens.Listeners.PlayerJoin;
import ca.cxtokens.Listeners.PlayerLeave;
import ca.cxtokens.Shop.Auction.AuctionHouse;
import ca.cxtokens.Shop.Auction.AuctionInteractionHandle;
import ca.cxtokens.Shop.Static.StaticInteractionHandle;
import ca.cxtokens.Vaults.VaultBlock;
import ca.cxtokens.Vaults.Vaults;

public final class CxTokens extends JavaPlugin {

    public Utils utils = new Utils(this);
    public MiscEvents events;
    public AuctionHouse auctionHouse;
    public HTTPUpdate httpUpdate;
    public HashMap<UUID, TokenPlayer> token_players;
    public Vaults vaults;
    public static String currency = "T$";

    @Override
    public void onEnable() {
        Storage.initializeData();
        this.token_players = new HashMap<>();

        // CxToken specific events
        this.events = new MiscEvents(this);
        if (Storage.config.getBoolean("auction.enabled", true)) {
            this.auctionHouse = new AuctionHouse(this);
        }

        if (Storage.config.getBoolean("config.http.enabled", false)) {
            this.httpUpdate = new HTTPUpdate(this);
        }

        // Vaults
        if (Storage.config.getBoolean("vaults.enabled", true)) {
            this.vaults = new Vaults(this);
        }

        // Events
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityKill(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerLeave(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDeath(this), this);
        this.getServer().getPluginManager().registerEvents(new Achievements(this), this);

        // Store handlers
        this.getServer().getPluginManager().registerEvents(new StaticInteractionHandle(this), this);
        this.getServer().getPluginManager().registerEvents(new AuctionInteractionHandle(this), this);

        // Commands
        this.getCommand("tlottery").setExecutor(new LotteryCommand(this));
        this.getCommand("tbal").setExecutor(new BalanceCommand(this));
        this.getCommand("tstore").setExecutor(new StoreCommand(this));
        this.getCommand("treset").setExecutor(new ResetCommand(this));
        this.getCommand("tsend").setExecutor(new SendCommand(this));
        this.getCommand("ttop").setExecutor(new BalTopCommand(this));
        this.getCommand("tbounty").setExecutor(new BountyCommand(this));
        this.getCommand("tauction").setExecutor(new AuctionCommand(this));
        this.getCommand("cxtokens").setExecutor(new AboutCommand());
        this.getCommand("tabout").setExecutor(new AboutCommand());
        this.getCommand("tadmin").setExecutor(new AdminCommand(this));
        this.getCommand("tsell").setExecutor(new SellCommand());
        this.getCommand("tvault").setExecutor(new VaultCommand(this));

        // Completers
        this.getCommand("tadmin").setTabCompleter(new AdminCompleter());
        this.getCommand("tauction").setTabCompleter(new AuctionCompleter());
        this.getCommand("tsend").setTabCompleter(new SendCompleter());
        this.getCommand("tbounty").setTabCompleter(new BountyCompleter());
        this.getCommand("tsell").setTabCompleter(new SellCompleter());

        // this is used if the plugin is reset and setup values for every player
        for (Player p : Bukkit.getOnlinePlayers()) {
            TokenPlayer.getTokenPlayer(this, p);
        }
    }

    @Override
    public void onDisable() {
        events.lottery.running = false;
        events.lottery.joinedPlayers.clear();
        this.token_players.clear();

        if (Storage.config.getBoolean("vaults.enabled", true)) {
            for (VaultBlock block : this.vaults.player_vaults) {
                block.clearArmorStands();
            }
            this.vaults.saveAllVaults();
            this.vaults.player_vaults.clear();
        }
    }
}
