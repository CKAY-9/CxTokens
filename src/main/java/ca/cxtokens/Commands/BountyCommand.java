package ca.cxtokens.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

public class BountyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        if (!Storage.config.getBoolean("bounty.enabled", true)) {
            sender.sendMessage(Utils.formatText("&cBounties are not enabled on this server!"));
            return false;
        }

        if (args.length <= 0) {
            sender.sendMessage(Utils.formatText("&c&lCommand Usage for " + command.getName() + ":"));
            sender.sendMessage(Utils.formatText("&c  <player> <number>"));
            return false;
        }
        
        try {
            String bountyPlayerName = args[0];
            Player bountyPlayer = Bukkit.getPlayer(bountyPlayerName);
            Player player = (Player) sender;

            if (bountyPlayer == null) {
                sender.sendMessage(Utils.formatText("&cThis player either doesn't exist or isn't online!"));
                return false;
            }

            boolean isSelf = bountyPlayer.getUniqueId().equals(player.getUniqueId());
            if (isSelf && !Storage.config.getBoolean("bounty.allowSelfBounty", true)) {
                sender.sendMessage(Utils.formatText("&cYou cannot place a bounty on yourself!"));
                return false;
            }

            long bountyPayout = Long.parseLong(args[1]);
            TokenPlayer me = TokenPlayer.convertPlayerToTokenPlayer(player);

            if (bountyPayout > Storage.config.getLong("bounty.maxBounty", Long.MAX_VALUE)) {
                sender.sendMessage(Utils.formatText("&cThe maximum bounty amount is " + CxTokens.currency + Storage.config.getLong("bounty.maxBounty", Long.MAX_VALUE) + "!"));
                return false;
            }

            if (bountyPayout < Storage.config.getLong("bounty.minBounty", 500)) {
                sender.sendMessage(Utils.formatText("&cThe minimum bounty amount is " + CxTokens.currency + Storage.config.getLong("bounty.minBounty", 500L) + "!"));
                return false;
            }

            if (me.getTokens() < bountyPayout) {
                sender.sendMessage(Utils.formatText("&cYou don't have enough tokens to set this bounty!"));
                return false;
            }

            if (me.getTokens() < 0) {
                sender.sendMessage(Utils.formatText("&cBounties can't be negative!"));
                return false;
            }

            TokenPlayer target = TokenPlayer.convertPlayerToTokenPlayer(bountyPlayer);

            if (target.hasBounty()) {
                sender.sendMessage(Utils.formatText("&cThis player already has a bounty!"));
                return false;
            }

            target.setBounty(bountyPayout, false);
            
            // data overwrites itself if the player is itself
            if (isSelf) {
                target.subtractTokens(bountyPayout, true);
            } else {
                me.subtractTokens(bountyPayout, true);
            }
            sender.sendMessage(Utils.formatText("&aSuccessfully placed the bounty!"));
        } catch (Exception ex) {
            Utils.getPlugin().getLogger().info(ex.toString());
            sender.sendMessage(Utils.formatText("&cError executing command: " + ex.getMessage()));
        } 
        
        return false;
    }
}
