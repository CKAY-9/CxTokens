package ca.cxtokens;

import java.io.IOException;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
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
        } else {
            removeBounty();
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

    public void setTokens(long overrideValue, boolean silent) {
        this.tokens = overrideValue;
        updateTokenPlayer();
        if (silent) {
            return;
        }

        this.ply.sendMessage(
                Utils.formatText("&aYour token balance has changed to &a&l" + CxTokens.currency + overrideValue));
    }

    public void addTokens(long tokensToAdd, boolean silent) {
        this.tokens += tokensToAdd;
        updateTokenPlayer();
        if (silent) {
            return;
        }

        this.ply.sendMessage(Utils.formatText("&aYou have recieved &a&l" + CxTokens.currency + tokensToAdd));
    }

    public void subtractTokens(long tokensToRemove, boolean silent) {
        this.tokens -= tokensToRemove;
        updateTokenPlayer();
        if (silent) {
            return;
        }

        this.ply.sendMessage(
                Utils.formatText("&cYour token balance decreased by &c&l" + CxTokens.currency + tokensToRemove));
    }

    public long getTokens() {
        return this.tokens;
    }

    public void reset(boolean silent) {
        if (!Storage.data.isSet("players." + ply.getUniqueId())) {
            return;
        }

        this.tokens = Storage.config.getLong("config.defaultTokenAmount", 500);
        updateTokenPlayer();
        if (silent) {
            return;
        }
        
        ply.sendMessage(Utils.formatText("&aSuccessfully reset your token account!"));
    }

    public void setBounty(long bountyPayout, boolean silent) {
        if (!silent) {
            if (!this.hasBounty()) {
                Bukkit.broadcastMessage(Utils.formatText("&cSomeone has placed a bounty on &c&l" + ply.getName()
                        + "&r&c for &c&l" + CxTokens.currency + bountyPayout + "&r&c!"));
            } else {
                Bukkit.broadcastMessage(Utils.formatText("&cSomeone has upped the bounty on &c&l" + ply.getName()
                        + "&r&c to &c&l" + CxTokens.currency + bountyPayout + "&r&c!"));
            }
        }

        this.bounty = bountyPayout;
        updateTokenPlayer();
        boolean show_in_name = Storage.config.getBoolean("bounty.showInName", true);
        String common_string = Utils.formatText("&c&l [BOUNTY: " + CxTokens.currency);
        boolean has_bounty_in_name = ply.getDisplayName().contains(common_string);
        if (show_in_name) {
            String display_name = ply.getDisplayName();
            String list_name = ply.getPlayerListName();
            if (has_bounty_in_name) {
                display_name = ply.getDisplayName().replaceAll(Utils.formatText("&c&l") + " \\[BOUNTY: " + Pattern.quote(CxTokens.currency) + "\\d+\\]", "").trim();
                list_name = ply.getPlayerListName().replaceAll(Utils.formatText("&c&l") + " \\[BOUNTY: " + Pattern.quote(CxTokens.currency) + "\\d+\\]", "").trim();
            }
            
            ply.setDisplayName(
                    display_name + Utils.formatText("&c&l [BOUNTY: " + CxTokens.currency + this.bounty + "]"));
            ply.setPlayerListName(list_name
                    + Utils.formatText("&c&l [BOUNTY: " + CxTokens.currency + this.bounty + "]"));
        }
    }

    public void removeBounty() {
        this.bounty = 0;
        updateTokenPlayer();
        String display_name = ply.getDisplayName().replaceAll(Utils.formatText("&c&l") + " \\[BOUNTY: " + Pattern.quote(CxTokens.currency) + "\\d+\\]", "").trim();
        String list_name = ply.getPlayerListName().replaceAll(Utils.formatText("&c&l") + " \\[BOUNTY: " + Pattern.quote(CxTokens.currency) + "\\d+\\]", "").trim();
        ply.setDisplayName(display_name);
        ply.setPlayerListName(list_name);
    }

    public long getBounty() {
        return this.bounty;
    }

    public boolean hasBounty() {
        return this.bounty > 0;
    }

    public static TokenPlayer convertPlayerToTokenPlayer(Player p) {
        if (!Storage.data.isSet("players." + p.getUniqueId())) {
            // Create new token data in file
            try {
                Storage.data.set("players." + p.getUniqueId() + ".tokens",
                        Storage.config.getInt("config.defaultTokenAmount", 500));
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
        
        return new TokenPlayer(Storage.data.getLong("players." + p.getUniqueId() + ".tokens"),
                Storage.data.getLong("players." + p.getUniqueId() + ".bounty"), p);
    }

    public static TokenPlayer getTokenPlayer(CxTokens tokens, Player player) {
        TokenPlayer token = tokens.token_players.get(player.getUniqueId());
        if (token == null) {
            token = convertPlayerToTokenPlayer(player);
            tokens.token_players.put(player.getUniqueId(), token);
        }

        return token;
    }
}
