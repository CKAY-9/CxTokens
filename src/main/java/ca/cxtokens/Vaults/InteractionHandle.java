package ca.cxtokens.Vaults;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import ca.cxtokens.CxTokens;
import ca.cxtokens.Storage;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

public class InteractionHandle implements Listener {
    private Vaults vaults;
    private CxTokens tokens;

    public InteractionHandle(CxTokens tokens, Vaults vaults) {
        this.tokens = tokens;
        this.vaults = vaults;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onVaultInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        if (block.getType() != Material.CHEST) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item != null && item.getType() != Material.AIR) {
            return;
        }

        VaultBlock vault = VaultBlock.vaultBlockFromBlock(block, vaults);
        if (vault == null) {
            return;
        }

        event.setCancelled(true);
        Action action = event.getAction();
        TokenPlayer token_player = TokenPlayer.getTokenPlayer(this.tokens, player);
        long min_deposit = Storage.config.getLong("vaults.minimum_deposit", 1);
        long max_deposit = Storage.config.getLong("vaults.maximum_deposit", 2500);
        long max_value = Storage.config.getLong("vaults.max_value", Long.MAX_VALUE);
        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (min_deposit > 0 && token_player.getTokens() < min_deposit) {
                player.sendMessage(Utils
                        .formatText("&cYou need at least " + CxTokens.currency + "" + min_deposit + " to deposit"));
                return;
            }

            long deposit_amount = 0;
            if (token_player.getTokens() < max_deposit) {
                deposit_amount = token_player.getTokens();
            } else {
                deposit_amount = max_deposit;
            }

            if (deposit_amount + vault.value >= max_value) {
                deposit_amount = max_value - vault.value;
                if (deposit_amount == 0) {
                    player.sendMessage(Utils
                        .formatText("&cMaximum value for this vault has been reached"));
                    return;
                }
            }

            token_player.subtractTokens(deposit_amount, true);
            vault.value += deposit_amount;
            player.sendMessage(Utils.formatText("&cDeposited &c&l" + CxTokens.currency + "" + deposit_amount));
        } else if (action == Action.LEFT_CLICK_BLOCK) {
            long withdraw_amount = 0;
            if (vault.value < max_deposit) {
                withdraw_amount = vault.value;
            } else {
                withdraw_amount = max_deposit;
            }

            token_player.addTokens(withdraw_amount, true);
            vault.value -= withdraw_amount;
            player.sendMessage(Utils.formatText("&aWithdrew &a&l" + CxTokens.currency + "" + withdraw_amount));
        }

        vault.updateArmorStandUI();
    }
}
