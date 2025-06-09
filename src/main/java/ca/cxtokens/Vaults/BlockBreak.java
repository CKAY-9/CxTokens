package ca.cxtokens.Vaults;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

public class BlockBreak implements Listener {
    private CxTokens tokens;
    private Vaults vaults;

    public BlockBreak(CxTokens tokens, Vaults vaults) {
        this.vaults = vaults;
        this.tokens = tokens;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        TokenPlayer token_player = TokenPlayer.getTokenPlayer(this.tokens, player);
        Block block = event.getBlock();

        VaultBlock vault = VaultBlock.vaultBlockFromBlock(block, this.vaults);
        if (vault == null) {
            return;
        }

        token_player.addTokens(vault.value, true);
        vault.value = 0;

        Player owner = Bukkit.getPlayer(vault.owner);
        if (owner != null && Storage.config.getBoolean("vaults.alert_on_break", true)) {
            owner.sendMessage(Utils.formatText("&cOne of your &c&lvaults&r&c has been &c&ldestroyed"));
        }

        for (int i = 0; i < this.vaults.player_vaults.size(); i++) {
            if (this.vaults.player_vaults.get(i).id == vault.id) {
                vault.clearArmorStands();
                this.vaults.player_vaults.remove(i);
                break;
            }
        }

        event.setDropItems(false);

        Storage.data.set("vaults." + vault.id, null);
        try {
            Storage.data.save(Storage.dataFile);
        } catch (IOException ex) {
            this.tokens.getLogger().warning(ex.toString());
        }
    }
}
