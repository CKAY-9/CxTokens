package ca.cxtokens.Commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;
import ca.cxtokens.Vaults.VaultBlock;

public class VaultCommand implements CommandExecutor {
    private CxTokens tokens;

    public VaultCommand(CxTokens tokens) {
        this.tokens = tokens;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Storage.config.getBoolean("vaults.enabled", true)) {
            sender.sendMessage(Utils.formatText("&c&lVaults&r&c are disabled on this server"));
            return false;
        }

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        TokenPlayer token_player = TokenPlayer.getTokenPlayer(this.tokens, player);
        long cost = Storage.config.getLong("vaults.cost", 0);
        if (cost > 0) {
            if (token_player.getTokens() < cost) {
                player.sendMessage(
                    Utils.formatText("&cYou need at least &c&l" + CxTokens.currency + "" + cost + "&r&c to spawn a vault"));
                return false;
            }
            token_player.subtractTokens(cost, false);
        }

        Location location = player.getLocation();
        World world = location.getWorld();
        Block block = world.getBlockAt(location);
        if (!block.isEmpty() && !block.isLiquid() && !block.isPassable()) {
            player.sendMessage(Utils.formatText("&cMust have a free block to spawn a vault"));
            return false;
        }
        
        player.sendMessage(Utils.formatText("&aGenerated &a&lVault&r&a. Interact using an &a&lempty hand"));
        player.sendMessage(Utils.formatText("&aLeft Click - &a&lWithdraw"));
        player.sendMessage(Utils.formatText("&aRight Click - &a&lDeposit"));


        block.setType(Material.CHEST);

        VaultBlock vault_block = new VaultBlock(player.getUniqueId(), 0, location, player.getName());
        this.tokens.vaults.saveVault(vault_block);

        return false;
    }

}
