package ca.cxtokens.Commands.Completers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import ca.cxtokens.Storage;

public class AdminCompleter implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            return null;
        }

        if (args.length == 1) {
            ArrayList<String> options = new ArrayList<>();
            options.add("set");
            options.add("add");
            options.add("subtract");
            options.add("reset");
            options.add("config");
            return options;
        }

        // Config handler
        if (args[0].equalsIgnoreCase("config")) {
            switch (args.length) {
                case 2: 
                    ArrayList<String> initialKeys = new ArrayList<>();
                    for (String initialKey : Storage.config.getKeys(false)) {
                        initialKeys.add(initialKey);
                    }
                    return initialKeys;
                case 3:
                    ArrayList<String> innerKeys = new ArrayList<>();
                    for (String innerKey : Storage.config.getConfigurationSection(args[1]).getKeys(false)) {
                        innerKeys.add(innerKey);
                    }
                    return innerKeys;
                case 4:
                    if (Storage.config.get(args[1] + "." + args[2]) == null) {
                        return Collections.singletonList("value");
                    }
                    return Collections.singletonList(Storage.config.get(args[1] + "." + args[2]).toString());
                default:
                    break;
            }
        }

        // Player managment handler
        if (args.length == 2 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("subtract"))) {
            ArrayList<String> players = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                players.add(p.getName());
            }
            return players;
        }
        if (args.length == 3 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("subtract"))) {
            return Collections.singletonList("0");
        }

        return null;
    }
}
