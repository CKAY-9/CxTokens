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

public class BountyCompleter implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            ArrayList<String> players = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                players.add(p.getName());
            }
            return players;
        }
        if (args.length == 2) {
            return Collections.singletonList(Long.toString(Storage.config.getLong("bounty.minBounty", 500L)));
        }

        return null;
    }
}