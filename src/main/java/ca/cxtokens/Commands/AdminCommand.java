package ca.cxtokens.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.cxtokens.CxTokens;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

public class AdminCommand implements CommandExecutor {
    CxTokens tokens;

    public AdminCommand(CxTokens tokens) {
        this.tokens = tokens;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (!sender.isOp()) {
            player.sendMessage(Utils.formatText("&cYou need to be an operator to run this command!"));
            return false;
        }

        if (args.length <= 0) {
            sender.sendMessage(Utils.formatText("&c&lCommand Usage for " + command.getName() + ":"));
            sender.sendMessage(Utils.formatText("&c  <set/add/subtract/reset> <player> <number>"));
            return false;
        }

        try {
            String toDo = args[0];
            Player target = Bukkit.getPlayer(args[1]);

            switch (toDo.toLowerCase()) {
                case "set":
                    TokenPlayer.convertPlayerToTokenPlayer(target).setTokens(Integer.parseInt(args[2]), true);
                    break;
                case "add":
                    TokenPlayer.convertPlayerToTokenPlayer(target).addTokens(Integer.parseInt(args[2]), true);
                    break;
                case "subtract":
                    TokenPlayer.convertPlayerToTokenPlayer(target).subtractTokens(Integer.parseInt(args[2]), true);
                    break;
                case "reset":
                    TokenPlayer.convertPlayerToTokenPlayer(target).reset(true);
                    break;
            }

            player.sendMessage(Utils.formatText("&aSuccessfully executed command!"));
        } catch (Exception ex) {
            Utils.getPlugin().getLogger().info(ex.toString());
            player.sendMessage(Utils.formatText("&cError executing command: " + ex.getMessage()));
        }
    
        return false;
    }
}
