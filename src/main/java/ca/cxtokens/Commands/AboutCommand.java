package ca.cxtokens.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ca.cxtokens.Utils;

public class AboutCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(Utils.formatText("&c&lCxTokens by CKAY9"));
        sender.sendMessage(Utils.formatText("&cCommands: "));
        sender.sendMessage(Utils.formatText("&c  - /ttop: Get the top players in terms of tokens"));
        sender.sendMessage(Utils.formatText("&c  - /tbounty: Set a bounty on a player"));
        sender.sendMessage(Utils.formatText("&c  - /tbal: Get the your or another player's amount of tokens"));
        sender.sendMessage(Utils.formatText("&c  - /tlottery: Join the lottery (one must be ongoing)"));
        sender.sendMessage(Utils.formatText("&c  - /treset: Reset your tokens to the server default"));
        sender.sendMessage(Utils.formatText("&c  - /tsend: Send tokens to a player"));
        sender.sendMessage(Utils.formatText("&c  - /tstore: Open the default Token Store"));
        sender.sendMessage(Utils.formatText("&c  - /tauction: Open the Auction House"));
        sender.sendMessage(Utils.formatText("&cGithub Repo: https://github.com/CKAY-9/CxTokens"));

        return false;
    }
}
