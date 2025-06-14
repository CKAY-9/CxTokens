package ca.cxtokens.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.cxtokens.CxTokens;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

public class BalanceCommand implements CommandExecutor {
    private CxTokens tokens;

    public BalanceCommand(CxTokens tokens) {
        this.tokens = tokens;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        if (args.length >= 2) {
            sender.sendMessage(Utils.formatText("&cCommand Usage for &c&l" + command.getName() + "&r&c:"));
            sender.sendMessage(Utils.formatText("&c <player/none>"));
            return false;
        }

        if (args.length >= 1) {
            Player p = Bukkit.getPlayerExact(args[0]);
            if (p == null) {
                sender.sendMessage(Utils.formatText("&cThe player with the name of &c&l" + args[0] + "&r&c either doesn't exist or isn't online!"));
                return false;
            }
            TokenPlayer t = TokenPlayer.getTokenPlayer(this.tokens, p);
            sender.sendMessage(Utils.formatText("&aThe current balance of " + p.getName() + " is &a&l" + CxTokens.currency + t.getTokens()));
        } else {
            // Local Player
            TokenPlayer t = TokenPlayer.getTokenPlayer(this.tokens, (Player) sender);
            sender.sendMessage(Utils.formatText("&aYour current balance is &a&l" + CxTokens.currency + t.getTokens()));
        }
        
        return false;
    }
}
