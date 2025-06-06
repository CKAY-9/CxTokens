package ca.cxtokens.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
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
            sender.sendMessage(Utils.formatText("&cCommand Usage for &c&l" + command.getName() + "&r&c:"));
            sender.sendMessage(Utils.formatText("&c  <set/add/subtract/reset> <player> <number>"));
            return false;
        }

        try {
            if (!args[0].equalsIgnoreCase("config")) {
                // Player handler
                String toDo = args[0];
                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    sender.sendMessage(Utils.formatText("&cCommand Usage for &c&l" + command.getName() + "&r&c:"));
                    sender.sendMessage(Utils.formatText("&c  <set/add/subtract/reset> <player> <number>"));
                    return false;
                }

                TokenPlayer token = TokenPlayer.getTokenPlayer(this.tokens, target);

                switch (toDo.toLowerCase()) {
                    case "set":
                        token.setTokens(Long.parseLong(args[2]), true);
                        break;
                    case "add":
                        token.addTokens(Long.parseLong(args[2]), true);
                        break;
                    case "subtract":
                        token.subtractTokens(Long.parseLong(args[2]), true);
                        break;
                    case "reset":
                        token.reset(true);
                        break;
                }

                player.sendMessage(Utils.formatText("&aSuccessfully executed command!"));
            } else {
                // Config handler
                String initialKey = args[1];
                String innerKey = args[2];
                String newValue = args[3];
                
                // :( i can't think of a cleaner way
                if (Utils.isBool(newValue)) {
                    Storage.config.set(initialKey + "." + innerKey, Boolean.parseBoolean(newValue));
                }
                if (Utils.isInteger(newValue)) {
                    Storage.config.set(initialKey + "." + innerKey, Integer.parseInt(newValue));
                }
                if (Utils.isLong(newValue)) {
                    Storage.config.set(initialKey + "." + innerKey, Long.parseLong(newValue));
                }
                if (Utils.isDouble(newValue)) {
                    Storage.config.set(initialKey + "." + innerKey, Double.parseDouble(newValue));
                }
                
                Storage.config.save(Storage.configFile);
                player.sendMessage(Utils.formatText("&aSuccessfully updated config!"));
            }
        } catch (Exception ex) {
            Utils.getPlugin().getLogger().info(ex.toString());
            player.sendMessage(Utils.formatText("&cError executing command: " + ex.getMessage()));
        }
    
        return false;
    }
}
