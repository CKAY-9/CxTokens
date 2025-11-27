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

    /**
     * Save the player's data to file
     */
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

    /**
     * Transfer an amount of tokens to this player from the sender, will check for errors or trying to send invalid values
     * 
     * @param sender The person who will be sending the money
     * @param amount The amount to send to this person
     */
    public void transferToPlayer(TokenPlayer sender, long amount) {

        if (amount < 0) {
            sender.ply.sendMessage(Utils.formatText("&cYou cannot send negative money!"));
            return;
        }

        if (sender.getTokens() < amount) {
            sender.ply.sendMessage(Utils.formatText("&cYou don't have enough tokens to send this amount!"));
            return;
        }

        this.addTokens(amount, true);
        sender.subtractTokens(amount, true);

        this.ply.sendMessage(Utils.formatText(
                "&aYou have recieved &a&l" + CxTokens.currency + amount + "&r&a from &a&l" + sender.ply.getName()));
        sender.ply.sendMessage(Utils.formatText(
                "&aYou sent &a&l" + CxTokens.currency + amount + "&r&a to &a&l" + this.ply.getName()));
    }

    /**
     * Set the tokens for the player
     * 
     * @param overrideValue Tokens to set
     * @param silent Should the player know their tokens were changed
     */
    public void setTokens(long overrideValue, boolean silent) {
        this.tokens = overrideValue;
        updateTokenPlayer();
        if (silent) {
            return;
        }

        this.ply.sendMessage(
                Utils.formatText("&aYour token balance has changed to &a&l" + CxTokens.currency + overrideValue));
    }

    /**
     * Add an amount of tokens to the player
     * 
     * @param tokensToAdd How many tokens to add
     * @param silent Should the player know their tokens increased
     */
    public void addTokens(long tokensToAdd, boolean silent) {
        this.tokens += tokensToAdd;
        updateTokenPlayer();
        if (silent) {
            return;
        }

        this.ply.sendMessage(Utils.formatText("&aYou have recieved &a&l" + CxTokens.currency + tokensToAdd));
    }

    /**
     * Subtract an amount of tokens from the player
     * 
     * @param tokensToRemove How many tokens to remove
     * @param silent Should the player know their tokens decreased
     */
    public void subtractTokens(long tokensToRemove, boolean silent) {
        this.tokens -= tokensToRemove;
        updateTokenPlayer();
        if (silent) {
            return;
        }

        this.ply.sendMessage(
                Utils.formatText("&cYour token balance decreased by &c&l" + CxTokens.currency + tokensToRemove));
    }

    /**
     * @return The player's tokens
     */
    public long getTokens() {
        return this.tokens;
    }

    /**
     * Reset the player's token account to the default config
     * 
     * @param silent Should the player know they have been reset
     */
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

    /**
     * Set the bounty for the player and update their name to include it
     * 
     * @param bountyPayout How much should the bounty for this player to be
     * @param silent Should the player know their bounty has changed
     */
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
                display_name = ply.getDisplayName().replaceAll(
                        Utils.formatText("&c&l") + " \\[BOUNTY: " + Pattern.quote(CxTokens.currency) + "\\d+\\]", "")
                        .trim();
                list_name = ply.getPlayerListName().replaceAll(
                        Utils.formatText("&c&l") + " \\[BOUNTY: " + Pattern.quote(CxTokens.currency) + "\\d+\\]", "")
                        .trim();
            }

            ply.setDisplayName(
                    display_name + Utils.formatText("&c&l [BOUNTY: " + CxTokens.currency + this.bounty + "]"));
            ply.setPlayerListName(list_name
                    + Utils.formatText("&c&l [BOUNTY: " + CxTokens.currency + this.bounty + "]"));
        }
    }

    /**
     * Set the player's bounty to 0 and reset their name
     */
    public void removeBounty() {
        this.bounty = 0;
        updateTokenPlayer();
        String display_name = ply.getDisplayName().replaceAll(
                Utils.formatText("&c&l") + " \\[BOUNTY: " + Pattern.quote(CxTokens.currency) + "\\d+\\]", "").trim();
        String list_name = ply.getPlayerListName().replaceAll(
                Utils.formatText("&c&l") + " \\[BOUNTY: " + Pattern.quote(CxTokens.currency) + "\\d+\\]", "").trim();
        ply.setDisplayName(display_name);
        ply.setPlayerListName(list_name);
    }

    /**
     * @return The player's current bounty
     */
    public long getBounty() {
        return this.bounty;
    }

    /**
     * @return Does this player have a bounty
     */
    public boolean hasBounty() {
        return this.bounty > 0;
    }

    /**
     * Creates and saves a new TokenPlayer
     * 
     * @param p The player to convert to a tokenPlayer
     * @return The new tokenPlayer
     */
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

    /**
     * @param tokens Contains the tokenPlayers hash map
     * @param player Who to match to a tokenPlayer
     * @return The already existing TokenPlayer in the token players hash map, or generate a new one and save it
     */
    public static TokenPlayer getTokenPlayer(CxTokens tokens, Player player) {
        TokenPlayer token = tokens.tokenPlayers.get(player.getUniqueId());
        if (token == null) {
            token = convertPlayerToTokenPlayer(player);
            tokens.tokenPlayers.put(player.getUniqueId(), token);
        }

        return token;
    }
}
