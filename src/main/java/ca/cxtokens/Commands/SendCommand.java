package ca.cxtokens.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.cxtokens.CxTokens;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;
import ca.cxtokens.Commands.Send.SendGUIHandler;

public class SendCommand implements CommandExecutor {
    private CxTokens tokens;
    private SendGUIHandler sendGUIHandler;

    public SendCommand(CxTokens tokens) {
        this.tokens = tokens;
        this.sendGUIHandler = new SendGUIHandler(this.tokens);
        this.tokens.getServer().getPluginManager().registerEvents(this.sendGUIHandler, tokens);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        if (args.length <= 1) {
            Player player = (Player)sender;
            this.sendGUIHandler.openTransferTargetSelection(player);
            return false;
        }
        
        try {
            TokenPlayer player = TokenPlayer.getTokenPlayer(this.tokens, (Player) sender);
            String targetPlayerName = args[0];
            long sendAmount = Long.parseLong(args[1]);
            Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
            if (targetPlayer == null) {
                return false;
            }

            TokenPlayer target = TokenPlayer.getTokenPlayer(this.tokens, targetPlayer);
            target.transferToPlayer(player, sendAmount);
        } catch (Exception ex) {
            Utils.getPlugin().getLogger().info(ex.toString());
            sender.sendMessage(Utils.formatText("&cError executing command: " + ex.getMessage()));
        }
        return false;
    }
}
