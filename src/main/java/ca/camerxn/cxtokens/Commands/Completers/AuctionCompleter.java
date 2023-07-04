package ca.camerxn.cxtokens.Commands.Completers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class AuctionCompleter implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            ArrayList<String> options = new ArrayList<>();
            options.add("sell");
            options.add("house");
            return options;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("sell")) {
            return Collections.singletonList("PRICE");
        }

        return null;
    }
}
