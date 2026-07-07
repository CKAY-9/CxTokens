package ca.cxtokens.Commands.Completers;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class AccountCompleter implements TabCompleter {

    public List<String> onTabComplete(
        CommandSender sender,
        Command command,
        String label,
        String[] args
    ) {
        int argumentCount = args.length;
        ArrayList<String> completion = new ArrayList<>();
        switch (argumentCount) {
            case 0:
            case 1:
                completion.add("create");
                completion.add("account_number");
                break;
            case 2:
                String firstArg = args[0].strip().toLowerCase();
                if (firstArg.equalsIgnoreCase("create")) {
                    // create new account
                    completion.add("account_name");
                } else {
                    // access account
                    completion.add("pin_code");
                }

                break;
            case 3:
                firstArg = args[0].strip().toLowerCase();
                if (firstArg.equalsIgnoreCase("create")) {
                    completion.add("pin_code");
                } else {
                    completion.add("view");
                    completion.add("deposit");
                    completion.add("withdraw");
                    completion.add("close");
                    completion.add("transactions");
                }

                break;
            case 4:
                completion.add("argument");
                break;
        }

        return completion;
    }
}
