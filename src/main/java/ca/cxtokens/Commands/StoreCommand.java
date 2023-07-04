package ca.cxtokens.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Shop.Static.Store;

public class StoreCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Store.openStaticStorePage(TokenPlayer.convertPlayerToTokenPlayer((Player) sender), 0);

        return false;
    }
}