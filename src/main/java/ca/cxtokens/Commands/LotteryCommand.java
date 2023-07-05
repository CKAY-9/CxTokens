package ca.cxtokens.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.cxtokens.Storage;
import ca.cxtokens.CxTokens;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

public class LotteryCommand implements CommandExecutor {
    private CxTokens tokens;

    public LotteryCommand(CxTokens tokens) {
        this.tokens = tokens;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Storage.config.getBoolean("lottery.enabled", true)) {
            sender.sendMessage(Utils.formatText("&cToken Lotteries are not enabled on this server!"));
            return false;
        }

        if (!this.tokens.events.lottery.running) {
            sender.sendMessage(Utils.formatText("&cThere is no active lottery going on!"));
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.formatText("&cYou must be a player to join the lottery!"));
            return false;
        }

        TokenPlayer player = TokenPlayer.convertPlayerToTokenPlayer((Player) sender);
        if (player.getTokens() < Storage.config.getInt("lottery.entryCost", 150)) {
            player.ply.sendMessage(Utils.formatText("&cYou don't have enough to cover the entry cost!"));
            return false;
        }

        if (this.tokens.events.lottery.joinedPlayers.contains(player)) {
            player.ply.sendMessage(Utils.formatText("&cYou are already in the lottery!"));
            return false;
        }

        player.subtractTokens(Storage.config.getInt("lottery.entryCost", 150), false);
        player.ply.sendMessage(Utils.formatText("&aYou have entered the lottery!"));
        this.tokens.events.lottery.joinedPlayers.add(player);
        
        return false;
    }

}
