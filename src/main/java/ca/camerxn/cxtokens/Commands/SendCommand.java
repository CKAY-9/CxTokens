package ca.camerxn.cxtokens.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.camerxn.cxtokens.TokenPlayer;
import ca.camerxn.cxtokens.Utils;

public class SendCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        
        try {
            TokenPlayer player = TokenPlayer.convertPlayerToTokenPlayer((Player) sender);
            String targetPlayer = args[0];
            int sendAmount = Integer.parseInt(args[1]);

            if (sendAmount < 0) {
                player.ply.sendMessage(Utils.formatText("&cYou cannot send negative money!"));
                return false;
            }
            if (player.getTokens() < sendAmount) {
                player.ply.sendMessage(Utils.formatText("&cYou don't have enough tokens to send this amount!"));
                return false;
            }
            if (Bukkit.getPlayerExact(targetPlayer) == null) {
                player.ply.sendMessage(Utils.formatText("&cThis player either doesn't exist or isn't online!"));
                return false;
            }

            TokenPlayer target = TokenPlayer.convertPlayerToTokenPlayer(Bukkit.getPlayer(targetPlayer));
            target.addTokens(sendAmount, true);
            player.subtractTokens(sendAmount, true);

            target.ply.sendMessage(Utils.formatText("&aYou have recieved T$" + sendAmount + " from " + player.ply.getName()));
            player.ply.sendMessage(Utils.formatText("&aYou sent T$" + sendAmount + " to " + target.ply.getName()));
        } catch (Exception ex) {
            Utils.getPlugin().getLogger().warning(ex.toString());
        }
        return false;
    }
}
