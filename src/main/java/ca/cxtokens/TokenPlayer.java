package ca.cxtokens;

import java.io.IOException;
import org.bukkit.entity.Player;

public class TokenPlayer {
    private int tokens;
    private int bounty;
    public Player ply;
    
    public TokenPlayer(int tokens, int bounty, Player ply) {
        this.tokens = tokens;
        this.bounty = bounty;
        this.ply = ply;

        if (hasBounty()) {
            setBounty(bounty, true);
        }
    }

    public void updateTokenPlayer() {
        try {
            Config.data.set("players." + this.ply.getUniqueId() + ".tokens", this.tokens);
            Config.data.set("players." + this.ply.getUniqueId() + ".bounty", this.bounty);
            Config.data.set("players." + this.ply.getUniqueId() + ".name", this.ply.getName());
            Config.data.save(Config.dataFile);
        } catch (IOException ex) {
            Utils.getPlugin().getLogger().warning(ex.toString());
        }
    }

    /*
     * 
     * TOKENS
     * 
     */
    public void setTokens(int overrideValue, boolean silent) {
        this.tokens = overrideValue;
        updateTokenPlayer();
        if (silent) return;
        this.ply.sendMessage(Utils.formatText("&aYour token balance has changed to T$" + overrideValue));
    }

    public void addTokens(int tokensToAdd, boolean silent) {
        this.tokens += tokensToAdd;
        updateTokenPlayer();
        if (silent) return;
        this.ply.sendMessage(Utils.formatText("&aYou have recieved T$" + tokensToAdd));
    }

    public void subtractTokens(int tokensToRemove, boolean silent) {
        this.tokens -= tokensToRemove;
        updateTokenPlayer();
        if (silent) return;
        this.ply.sendMessage(Utils.formatText("&cYour token balance decreased by T$" + tokensToRemove));
    }
    
    public int getTokens() {
        return Config.data.getInt("players." + ply.getUniqueId() + ".tokens", Config.config.getInt("config.defaultTokenAmount", 500));
    }

    public void reset(boolean silent) {
        if (!Config.data.isSet("players." + ply.getUniqueId())) return;
        this.tokens = Config.config.getInt("config.defaultTokenAmount", 500);
        updateTokenPlayer();
        if (silent) return;
        ply.sendMessage(Utils.formatText("&aSuccessfully reset your token account!"));
    }

    /*
     * 
     * BOUNTIES
     * 
     */
    public void setBounty(int bountyPayout, boolean silent) {
        this.bounty = bountyPayout;
        updateTokenPlayer();
        if (Config.config.getBoolean("bounty.showInName", true)) {
            ply.setDisplayName(ply.getName() + Utils.formatText("&c&l [BOUNTY: T$" + this.bounty + "]"));
            ply.setPlayerListName(ply.getName() + Utils.formatText("&c&l [BOUNTY: T$" + this.bounty + "]"));
        }
        if (silent) return;
        ply.sendMessage(Utils.formatText("&c&lSomeone has placed a bounty on you for T$" + bountyPayout + "!"));
    }

    public void removeBounty() {
        this.bounty = 0;
        updateTokenPlayer();
        ply.setDisplayName(ply.getName());
        ply.setPlayerListName(ply.getName());
    }

    public int getBounty() {
        return this.bounty;
    }

    public boolean hasBounty() {
        return this.bounty > 0;
    }

    /*
     * 
     * CREATING
     * 
     */
    public static TokenPlayer convertPlayerToTokenPlayer(Player p) {
        if (!Config.data.isSet("players." + p.getUniqueId())) {
            // Create new token data in file
            try {
                Config.data.set("players." + p.getUniqueId() + ".tokens", Config.config.getInt("config.defaultTokenAmount", 500));
                Config.data.set("players." + p.getUniqueId() + ".name", p.getName());
                Config.data.save(Config.dataFile);
            } catch (IOException ex) {
                Utils.getPlugin().getLogger().warning(ex.toString());
                return new TokenPlayer(0, 0, p);
            }
        }
        if (!Config.data.isSet("players." + p.getUniqueId() + ".bounty")) {
            try {
                Config.data.set("players." + p.getUniqueId() + ".bounty", 0);
                Config.data.save(Config.dataFile);
            } catch (IOException ex) {
                Utils.getPlugin().getLogger().warning(ex.toString());
                return new TokenPlayer(0, 0, p);
            }
        }
        return new TokenPlayer(Config.data.getInt("players." + p.getUniqueId() + ".tokens"), Config.data.getInt("players." + p.getUniqueId() + ".bounty"), p);
    }
}
