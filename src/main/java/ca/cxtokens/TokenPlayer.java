package ca.cxtokens;

import java.io.IOException;
import org.bukkit.entity.Player;

public class TokenPlayer {
    private long tokens;
    private long bounty;
    public Player ply;
    
    public TokenPlayer(long tokens, long bounty, Player ply) {
        this.tokens = tokens;
        this.bounty = bounty;
        this.ply = ply;

        if (hasBounty()) {
            setBounty(bounty, true);
        }
    }

    public void updateTokenPlayer() {
        try {
            Storage.data.set("players." + this.ply.getUniqueId() + ".tokens", this.tokens);
            Storage.data.set("players." + this.ply.getUniqueId() + ".bounty", this.bounty);
            Storage.data.set("players." + this.ply.getUniqueId() + ".name", this.ply.getName());
            Storage.data.save(Storage.dataFile);
        } catch (IOException ex) {
            Utils.getPlugin().getLogger().warning(ex.toString());
        }
    }

    /*
     * 
     * TOKENS
     * 
     */
    public void setTokens(long overrideValue, boolean silent) {
        this.tokens = overrideValue;
        updateTokenPlayer();
        if (silent) return;
        this.ply.sendMessage(Utils.formatText("&aYour token balance has changed to &a&l" + CxTokens.currency + overrideValue));
    }

    public void addTokens(long tokensToAdd, boolean silent) {
        this.tokens += tokensToAdd;
        updateTokenPlayer();
        if (silent) return;
        this.ply.sendMessage(Utils.formatText("&aYou have recieved &a&l" + CxTokens.currency + tokensToAdd));
    }

    public void subtractTokens(long tokensToRemove, boolean silent) {
        this.tokens -= tokensToRemove;
        updateTokenPlayer();
        if (silent) return;
        this.ply.sendMessage(Utils.formatText("&cYour token balance decreased by &c&l" + CxTokens.currency + tokensToRemove));
    }
    
    public long getTokens() {
        return Storage.data.getLong("players." + ply.getUniqueId() + ".tokens", Storage.config.getInt("config.defaultTokenAmount", 500));
    }

    public void reset(boolean silent) {
        if (!Storage.data.isSet("players." + ply.getUniqueId())) return;
        this.tokens = Storage.config.getLong("config.defaultTokenAmount", 500);
        updateTokenPlayer();
        if (silent) return;
        ply.sendMessage(Utils.formatText("&aSuccessfully reset your token account!"));
    }

    /*
     * 
     * BOUNTIES
     * 
     */
    public void setBounty(long bountyPayout, boolean silent) {
        this.bounty = bountyPayout;
        updateTokenPlayer();
        if (Storage.config.getBoolean("bounty.showInName", true)) {
            ply.setDisplayName(ply.getName() + Utils.formatText("&c&l [BOUNTY: " + CxTokens.currency + this.bounty + "]"));
            ply.setPlayerListName(ply.getName() + Utils.formatText("&c&l [BOUNTY: " + CxTokens.currency + this.bounty + "]"));
        }
        if (silent) return;
        ply.sendMessage(Utils.formatText("&cSomeone has placed a bounty on you for &c&l" + CxTokens.currency + bountyPayout + "&r&c!"));
    }

    public void removeBounty() {
        this.bounty = 0;
        updateTokenPlayer();
        ply.setDisplayName(ply.getName());
        ply.setPlayerListName(ply.getName());
    }

    public long getBounty() {
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
        if (!Storage.data.isSet("players." + p.getUniqueId())) {
            // Create new token data in file
            try {
                Storage.data.set("players." + p.getUniqueId() + ".tokens", Storage.config.getInt("config.defaultTokenAmount", 500));
                Storage.data.set("players." + p.getUniqueId() + ".name", p.getName());
                Storage.data.save(Storage.dataFile);
            } catch (IOException ex) {
                Utils.getPlugin().getLogger().warning(ex.toString());
                return new TokenPlayer(0, 0, p);
            }
        }
        if (!Storage.data.isSet("players." + p.getUniqueId() + ".bounty")) {
            try {
                Storage.data.set("players." + p.getUniqueId() + ".bounty", 0);
                Storage.data.save(Storage.dataFile);
            } catch (IOException ex) {
                Utils.getPlugin().getLogger().warning(ex.toString());
                return new TokenPlayer(0, 0, p);
            }
        }
        return new TokenPlayer(Storage.data.getLong("players." + p.getUniqueId() + ".tokens"), Storage.data.getLong("players." + p.getUniqueId() + ".bounty"), p);
    }
}
