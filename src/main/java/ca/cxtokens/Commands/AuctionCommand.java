package ca.cxtokens.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.cxtokens.CxTokens;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

public class AuctionCommand implements CommandExecutor {
    CxTokens tokens;

    public AuctionCommand(CxTokens tokens) {
        this.tokens = tokens;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        if (args.length <= 0) {
            sender.sendMessage(Utils.formatText("&c&lCommand Usage for " + command.getName() + ":"));
            sender.sendMessage(Utils.formatText("&c  <house/sell> <number>"));
            return false;
        }

        try {
            String selectedView = args[0];
            switch (selectedView.toLowerCase()) {
                case "house":
                    this.tokens.auctionHouse.openAuctionHouse((Player) sender, 0);
                    break;
                case "sell":
                    long sellPrice = Long.parseLong(args[1]);
                    this.tokens.auctionHouse.sellItemOnHouse(TokenPlayer.convertPlayerToTokenPlayer((Player) sender), sellPrice);
                    break;
            }
        } catch (Exception ex) {
            Utils.getPlugin().getLogger().info(ex.toString());
            sender.sendMessage(Utils.formatText("&cError executing command: " + ex.getMessage()));
        }

        return false;
    }
}
