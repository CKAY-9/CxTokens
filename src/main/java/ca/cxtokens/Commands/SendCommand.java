package ca.cxtokens.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.cxtokens.CxTokens;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

public class SendCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        if (args.length <= 0) {
            sender.sendMessage(Utils.formatText("&c&lCommand Usage for " + command.getName() + ":"));
            sender.sendMessage(Utils.formatText("&c  <player> <number>"));
            return false;
        }
        
        try {
            TokenPlayer player = TokenPlayer.convertPlayerToTokenPlayer((Player) sender);
            String targetPlayer = args[0];
            long sendAmount = Long.parseLong(args[1]);

            if (sendAmount < 0) {
                player.ply.sendMessage(Utils.formatText("&cYou cannot send negative money!"));
                return false;
            }
            if (player.getTokens() < sendAmount) {
                player.ply.sendMessage(Utils.formatText("&cYou don't have enough tokens to send this amount!"));
                return false;
            }
            if (Bukkit.getPlayer(targetPlayer) == null) {
                player.ply.sendMessage(Utils.formatText("&cThis player either doesn't exist or isn't online!"));
                return false;
            }
            if (Bukkit.getPlayer(targetPlayer).getUniqueId().equals(player.ply.getUniqueId())) {
                player.ply.sendMessage(Utils.formatText("&cYou cannot send money to yourself!"));
                return false;
            }

            TokenPlayer target = TokenPlayer.convertPlayerToTokenPlayer(Bukkit.getPlayer(targetPlayer));
            target.addTokens(sendAmount, true);
            player.subtractTokens(sendAmount, true);

            target.ply.sendMessage(Utils.formatText("&aYou have recieved &a&l" + CxTokens.currency + sendAmount + "&r&a from &a&l" + player.ply.getName()));
            player.ply.sendMessage(Utils.formatText("&aYou sent &a&l" + CxTokens.currency + sendAmount + "&r&a to &a&l" + target.ply.getName()));
        } catch (Exception ex) {
            Utils.getPlugin().getLogger().info(ex.toString());
            sender.sendMessage(Utils.formatText("&cError executing command: " + ex.getMessage()));
        }
        return false;
    }
}
