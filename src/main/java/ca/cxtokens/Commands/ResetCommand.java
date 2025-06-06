package ca.cxtokens.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ca.cxtokens.CxTokens;
import ca.cxtokens.TokenPlayer;

public class ResetCommand implements CommandExecutor {
    private CxTokens tokens;

    public ResetCommand(CxTokens tokens) {
        this.tokens = tokens;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        TokenPlayer.getTokenPlayer(this.tokens, (Player) sender).reset(false);
        return false;
    }
}
