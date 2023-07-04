package ca.camerxn.cxtokens;

import java.io.IOException;
import org.bukkit.entity.Player;

public class TokenPlayer {
    private int tokens;
    public Player ply;
    
    public TokenPlayer(int tokens, Player ply) {
        this.tokens = tokens;
        this.ply = ply;
    }

    public int getTokens() {
        return Config.data.getInt("players." + ply.getUniqueId() + ".tokens");
    }

    // Save tokens to file
    public void updateTokens() {
        try {
            Config.data.set("players." + this.ply.getUniqueId() + ".tokens", this.tokens);
            Config.data.set("players." + this.ply.getUniqueId() + ".name", this.ply.getName());
            Config.data.save(Config.dataFile);
        } catch (IOException ex) {
            Utils.getPlugin().getLogger().warning(ex.toString());
        }
    }

    public void setTokens(int overrideValue, boolean silent) {
        this.tokens = overrideValue;
        updateTokens();
        if (silent) return;
        this.ply.sendMessage(Utils.formatText("&aYour token balance has changed to T$" + overrideValue));
    }

    public void addTokens(int tokensToAdd, boolean silent) {
        this.tokens += tokensToAdd;
        updateTokens();
        if (silent) return;
        this.ply.sendMessage(Utils.formatText("&aYou have recieved T$" + tokensToAdd));
    }

    public void subtractTokens(int tokensToRemove, boolean silent) {
        this.tokens -= tokensToRemove;
        updateTokens();
        if (silent) return;
        this.ply.sendMessage(Utils.formatText("&cYour token balance decreased by T$" + tokensToRemove));
    }

    public static TokenPlayer convertPlayerToTokenPlayer(Player p) {
        if (!Config.data.isSet("players." + p.getUniqueId())) {
            // Create new token data in file
            try {
                Config.data.set("players." + p.getUniqueId() + ".tokens", Config.data.getInt("config.defaultTokenAmount"));
                Config.data.set("players." + p.getUniqueId() + ".name", p.getName());
                Config.data.save(Config.dataFile);
            } catch (IOException ex) {
                Utils.getPlugin().getLogger().warning(ex.toString());
                return new TokenPlayer(0, p);
            }
        }
        return new TokenPlayer(Config.data.getInt("players." + p.getUniqueId() + ".tokens"), p);
    }
}
