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
            sender.sendMessage(Utils.formatText("&c&lLotteries &r&care disabled on this server"));
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

        TokenPlayer player = TokenPlayer.getTokenPlayer(this.tokens, (Player) sender);
        if (player.getTokens() < Storage.config.getLong("lottery.entryCost", 150L)) {
            player.ply.sendMessage(Utils.formatText("&cYou don't have enough to cover the entry cost!"));
            return false;
        }

        for (TokenPlayer temp : this.tokens.events.lottery.joinedPlayers) {
            if (temp.ply.getUniqueId() == player.ply.getUniqueId()) {
                player.ply.sendMessage(Utils.formatText("&cYou are already in the lottery!"));
                return false;
            }
        }

        player.subtractTokens(Storage.config.getLong("lottery.entryCost", 150L), false);
        player.ply.sendMessage(Utils.formatText("&aYou have entered the lottery!"));
        this.tokens.events.lottery.joinedPlayers.add(player);
        
        return false;
    }

}
