package ca.camerxn.cxtokens;

import org.bukkit.plugin.java.JavaPlugin;
import ca.camerxn.cxtokens.Events.PlayerJoin;

public final class CxTokens extends JavaPlugin {

    public Utils utils = new Utils(this);

    @Override
    public void onEnable() {
        // Events
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(), this);

        // Commands

        Config.initializeData();   
    }

    @Override
    public void onDisable() {
    
    }
}
