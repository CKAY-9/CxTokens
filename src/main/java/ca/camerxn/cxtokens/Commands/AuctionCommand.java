package ca.camerxn.cxtokens.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.camerxn.cxtokens.CxTokens;
import ca.camerxn.cxtokens.TokenPlayer;

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

        try {
            String selectedView = args[0];
            switch (selectedView.toLowerCase()) {
                case "house":
                    this.tokens.auctionHouse.openAuctionHouse((Player) sender, 0);
                    break;
                case "sell":
                    int sellPrice = Integer.parseInt(args[1]);
                    this.tokens.auctionHouse.sellItemOnHouse(TokenPlayer.convertPlayerToTokenPlayer((Player) sender), sellPrice);
                    break;
            }
        } catch (Exception ex) {
            this.tokens.getLogger().warning(ex.toString());
        }

        return false;
    }
}
