package ca.cxtokens.Vaults;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;

public class Vaults {
    private CxTokens tokens;
    public ArrayList<VaultBlock> player_vaults;

    public Vaults(CxTokens tokens) {
        this.tokens = tokens;
        this.player_vaults = new ArrayList<>();
        this.loadPlayerVaults();

        this.tokens.getServer().getPluginManager().registerEvents(
                new InteractionHandle(this.tokens, this), this.tokens);
        this.tokens.getServer().getPluginManager().registerEvents(
                new BlockBreak(this.tokens, this), this.tokens);
    }

    public void saveVault(VaultBlock vault_block) {
        this.player_vaults.add(vault_block);
    }

    public void loadPlayerVaults() {
        ConfigurationSection vaults = Storage.data.getConfigurationSection("vaults");
        if (vaults == null) {
            return;
        }

        for (String vault_key : vaults.getKeys(false)) {
            ConfigurationSection vault_section = vaults.getConfigurationSection(vault_key);

            UUID owner = UUID.fromString(vault_section.getString("owner", ""));
            long value = vault_section.getLong("value", 0);
            String player_name = vault_section.getString("name", "");
            int x = vault_section.getInt("location.x", 0);
            int y = vault_section.getInt("location.y", 0);
            int z = vault_section.getInt("location.z", 0);
            String world_name = vault_section.getString("world_name", "world");
            World world = Bukkit.getWorld(world_name);
            if (world == null) {
                continue;
            }

            VaultBlock vault_block = new VaultBlock(owner, value, new Location(world, x, y, z), player_name);
            saveVault(vault_block);
        }
    }

    public void saveAllVaults() {
        for (VaultBlock vault_block : this.player_vaults) {
            Storage.data.set("vaults." + vault_block.id + ".owner", vault_block.owner.toString());
            Storage.data.set("vaults." + vault_block.id + ".name", vault_block.player_name);
            Storage.data.set("vaults." + vault_block.id + ".value", vault_block.value);
            Storage.data.set("vaults." + vault_block.id + ".world_name", vault_block.world_name);
            Storage.data.set("vaults." + vault_block.id + ".location.x", vault_block.location.getBlockX());
            Storage.data.set("vaults." + vault_block.id + ".location.y", vault_block.location.getBlockY());
            Storage.data.set("vaults." + vault_block.id + ".location.z", vault_block.location.getBlockZ());
        }

        try {
            Storage.data.save(Storage.dataFile);
        } catch (IOException ex) {
            this.tokens.getLogger().warning(ex.toString());
        }
    }
}
