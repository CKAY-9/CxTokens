package ca.cxtokens.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;
import ca.cxtokens.Shop.Static.Store;

public class StoreCommand implements CommandExecutor {
    private CxTokens tokens;

    public StoreCommand(CxTokens tokens) {
        this.tokens = tokens;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        if (!Storage.config.getBoolean("static_store.enabled", true)) {
            sender.sendMessage(Utils.formatText("&c&lTStore &r&cis disabled on this server"));
            return false;
        }

        Store.openStaticStorePage(TokenPlayer.getTokenPlayer(tokens, (Player) sender), 0);

        return false;
    }
}