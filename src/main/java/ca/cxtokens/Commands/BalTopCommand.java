package ca.cxtokens.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

public class BalTopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Stolen from me https://github.com/Camerxxn/The-Walls/blob/main/src/main/java/ca/camerxn/thewalls/Commands/WLeaderboard.java
        
        HashMap<String, Long> leadeboardEntries = new HashMap<>();
        LinkedHashMap<String, Long> sortedMap = new LinkedHashMap<>();
        ArrayList<Long> list = new ArrayList<>();
        if (Storage.config.getBoolean("config.useOnlinePlayersOnlyForBalTop", true)) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                leadeboardEntries.put(p.getName(), TokenPlayer.convertPlayerToTokenPlayer(p).getTokens());
            }
        } else {
            for (String s : Storage.data.getConfigurationSection("players").getKeys(false)) {
                leadeboardEntries.put(Storage.data.getString("players." + s + ".name"), Storage.data.getLong("players." + s + ".tokens"));
            }
        }

        if (leadeboardEntries.size() <= 0) {
            sender.sendMessage(Utils.formatText("&6&l-------- BAL TOP --------"));
            sender.sendMessage(Utils.formatText("&cNO PLAYER DATA AVAILABLE"));
            sender.sendMessage(Utils.formatText("&6&l-------------------------"));
            return false;
        }

        for (Map.Entry<String, Long> entry : leadeboardEntries.entrySet()) {
            list.add(entry.getValue());
        }
        Collections.sort(list); 
        Collections.reverse(list);
        for (long num : list) {
            for (Entry<String, Long> entry : leadeboardEntries.entrySet()) {
                if (entry.getValue().equals(num)) {
                    sortedMap.put(entry.getKey(), num);
                }
            }
        }
        sender.sendMessage(Utils.formatText("&6&l-------- BAL TOP --------"));
        int index = 0;
        for (String name : sortedMap.keySet()) {
            if (index == 5) break;
            sender.sendMessage(Utils.formatText("&5" + name + " - &9" + CxTokens.currency + sortedMap.get(name)));
            index++;
        }
        sender.sendMessage(Utils.formatText("&6&l-------------------------"));

        return false;
    }
}
