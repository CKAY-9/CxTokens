package ca.cxtokens.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;
import ca.cxtokens.Shop.Auction.Viewer;

public class AuctionCommand implements CommandExecutor {
    CxTokens tokens;

    public AuctionCommand(CxTokens tokens) {
        this.tokens = tokens;
    }

    private void setupHouseForPlayer(Player p) {
        for (Viewer viewer : this.tokens.auctionHouse.viewers) {
            if (viewer.player.getUniqueId().toString().equals(p.getUniqueId().toString())) {
                viewer.openPage(0);
                return;
            }
        }

        Viewer newViewer = new Viewer(p, 0, this.tokens.auctionHouse);
        this.tokens.auctionHouse.viewers.add(newViewer);
        newViewer.openPage(0);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Storage.config.getBoolean("auction.enabled", true)) {
            sender.sendMessage(Utils.formatText("&cThe Auction House isn't enabled on this server!"));
            return false;
        }

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
                    setupHouseForPlayer((Player) sender);
                    break;
                case "sell":
                    long sellPrice = Long.parseLong(args[1]);
                    this.tokens.auctionHouse.sellItemOnHouse(TokenPlayer.convertPlayerToTokenPlayer((Player) sender),
                            sellPrice);
                    break;
            }
        } catch (Exception ex) {
            Utils.getPlugin().getLogger().info(ex.toString());
            sender.sendMessage(Utils.formatText("&cError executing command: " + ex.getMessage()));
        }

        return false;
    }
}
