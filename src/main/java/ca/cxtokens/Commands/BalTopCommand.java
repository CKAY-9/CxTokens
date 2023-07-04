package ca.cxtokens.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ca.cxtokens.Config;
import ca.cxtokens.Utils;

public class BalTopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Stolen from me https://github.com/Camerxxn/The-Walls/blob/main/src/main/java/ca/camerxn/thewalls/Commands/WLeaderboard.java
        
        HashMap<String, Integer> leadeboardEntries = new HashMap<>();
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        ArrayList<Integer> list = new ArrayList<>();
        for (String s : Config.data.getConfigurationSection("players").getKeys(false)) {
            Utils.getPlugin().getLogger().info(s);
            leadeboardEntries.put(Config.data.getString("players." + s + ".name"), Config.data.getInt("players." + s + ".tokens"));
        }
        for (Map.Entry<String, Integer> entry : leadeboardEntries.entrySet()) {
            list.add(entry.getValue());
        }
        Collections.sort(list); 
        Collections.reverse(list);
        for (int num : list) {
            for (Entry<String, Integer> entry : leadeboardEntries.entrySet()) {
                if (entry.getValue().equals(num)) {
                    sortedMap.put(entry.getKey(), num);
                }
            }
        }
        sender.sendMessage(Utils.formatText("&6&l-------- BAL TOP --------"));
        int index = 0;
        for (String name : sortedMap.keySet()) {
            if (index == 5) break;
            sender.sendMessage(Utils.formatText("&5" + name + " - &9T$" + sortedMap.get(name)));
            index++;
        }
        sender.sendMessage(Utils.formatText("&6&l-------------------------"));

        return false;
    }
}
