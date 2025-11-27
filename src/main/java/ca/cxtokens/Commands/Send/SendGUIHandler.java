package ca.cxtokens.Commands.Send;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import ca.cxtokens.CxTokens;
import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

class SendTransfer {
    TokenPlayer player;
    TokenPlayer transferTarget;
    long amount;

    /**
     * @param player The player who wants to send money
     */
    public SendTransfer(TokenPlayer player) {
        this.player = player;
        this.transferTarget = null;
        this.amount = 0;
    }

    /**
     * @param player         The player who wants to send money
     * @param transferTarget The player who will receive the money
     */
    public SendTransfer(TokenPlayer player, TokenPlayer transferTarget) {
        this.player = player;
        this.transferTarget = transferTarget;
        this.amount = 0;
    }

    /**
     * @param player         The player who wants to send money
     * @param transferTarget The player who will receive the money
     * @param amount         The amount to send
     */
    public SendTransfer(TokenPlayer player, TokenPlayer transferTarget, long amount) {
        this.player = player;
        this.transferTarget = transferTarget;
        this.amount = amount;
    }
}

public class SendGUIHandler implements Listener {
    private CxTokens tokens;
    private HashMap<UUID, SendTransfer> transfers;

    // Button Slots
    private int SET_TO_ZERO = 10;
    private int DECREASE_FIVE_HUNDRED = 11;
    private int DECREASE_HUNDRED = 12;
    private int SEND = 13;
    private int INCREASE_HUNDRED = 14;
    private int INCREASE_FIVE_HUNDRED = 15;
    private int SET_TO_MAX = 16;

    public SendGUIHandler(CxTokens tokens) {
        this.tokens = tokens;
        this.transfers = new HashMap<>();
    }

    /**
     * Go to the next page of players available
     *
     * @param inv  The existing target inventory we want to clear and get the next
     *             possible targets
     * @param page The page index we want to go to (e.g. 1: players indicies 45-90,
     *             0: players indicies 0-44)
     */
    private void goToTargetPlayersPage(Player player, int page) {
        Inventory inv = Bukkit.createInventory(null, 54,
                Utils.formatText("&a&lSelect Transfer Target - " + (page + 1)));
        inv.clear();

        ItemStack backOrExit = new ItemStack(Material.RED_CONCRETE, 1);
        ItemMeta boeMeta = backOrExit.getItemMeta();
        if (page == 0) {
            boeMeta.setDisplayName(Utils.formatText("&cExit"));
        } else {
            boeMeta.setDisplayName(Utils.formatText("&cPrevious Page"));
        }
        backOrExit.setItemMeta(boeMeta);
        inv.setItem(Utils.LARGE_EXIT_PREVIOUS_SLOT, backOrExit);

        Player[] onlinePlayers = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        int start = page * 45;
        int end = 45;
        // prevent going over all the possible players
        if (start + end >= onlinePlayers.length) {
            end = onlinePlayers.length;
        } else {
            // add a next button for next page
            ItemStack next = new ItemStack(Material.GREEN_CONCRETE, 1);
            ItemMeta nextMeta = backOrExit.getItemMeta();
            nextMeta.setDisplayName(Utils.formatText("&aNext Page"));
            next.setItemMeta(nextMeta);
            inv.setItem(Utils.LARGE_NEXT_PAGE_SLOT, next);
        }

        int invPosition = 0;
        for (int i = start; i < end; i++) {
            Player p = onlinePlayers[i];
            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            meta.setDisplayName(p.getName());
            meta.setOwnerProfile(p.getPlayerProfile());
            itemStack.setItemMeta(meta);

            // set the head in the inventory
            inv.setItem(invPosition, itemStack);
            invPosition++;
        }

        player.closeInventory();
        player.openInventory(inv);
    }

    /**
     * Creates a transfer and opens the GUI menu for a player to select a target to
     * send money to
     *
     * @param player Who to open the GUI for
     */
    public void openTransferTargetSelection(Player player) {
        this.createTransfer(player);
        this.goToTargetPlayersPage(player, 0);
    }

    /**
     * Open the GUI menu for a player to select how much money to send to the
     * selected target,
     * found in the player's SendTransfer
     *
     * @param player Who to open the GUI for
     */
    public void openTransferAmountSelection(Player player) {
        SendTransfer transfer = this.transfers.get(player.getUniqueId());
        if (transfer == null) {
            player.closeInventory();
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 27, Utils.formatText("&a&lSelect Transfer Amount"));
        inv.clear();

        ItemStack backOrExit = new ItemStack(Material.RED_CONCRETE, 1);
        ItemMeta boeMeta = backOrExit.getItemMeta();
        boeMeta.setDisplayName(Utils.formatText("&cCancel"));
        backOrExit.setItemMeta(boeMeta);
        inv.setItem(Utils.SMALL_EXIT_PREVIOUS_SLOT, backOrExit);

        ItemStack setZero = new ItemStack(Material.BROWN_STAINED_GLASS, 1);
        ItemMeta szMeta = setZero.getItemMeta();
        szMeta.setDisplayName(Utils.formatText("&4Set to &l" + CxTokens.currency + "0"));
        setZero.setItemMeta(szMeta);
        inv.setItem(SET_TO_ZERO, setZero);

        ItemStack decreaseFiveHundred = new ItemStack(Material.RED_STAINED_GLASS, 1);
        ItemMeta dfhMeta = decreaseFiveHundred.getItemMeta();
        dfhMeta.setDisplayName(Utils.formatText("&cDecrease by &l" + CxTokens.currency + "500"));
        decreaseFiveHundred.setItemMeta(dfhMeta);
        inv.setItem(DECREASE_FIVE_HUNDRED, decreaseFiveHundred);

        ItemStack decreaseHundred = new ItemStack(Material.ORANGE_STAINED_GLASS, 1);
        ItemMeta dhMeta = decreaseHundred.getItemMeta();
        dhMeta.setDisplayName(Utils.formatText("&6Decrease by &l" + CxTokens.currency + "100"));
        decreaseHundred.setItemMeta(dhMeta);
        inv.setItem(DECREASE_HUNDRED, decreaseHundred);

        ItemStack send = new ItemStack(Material.PAPER, 1);
        ItemMeta sendMeta = send.getItemMeta();
        sendMeta.setDisplayName(Utils
                .formatText("&bSend &b&l" + CxTokens.currency + "" + transfer.amount + "&r&b to "
                        + transfer.transferTarget.ply.getName()));
        send.setItemMeta(sendMeta);
        inv.setItem(SEND, send);

        ItemStack increaseHundred = new ItemStack(Material.GREEN_STAINED_GLASS, 1);
        ItemMeta ihMeta = increaseHundred.getItemMeta();
        ihMeta.setDisplayName(Utils.formatText("&2Increase by &l" + CxTokens.currency + "100"));
        increaseHundred.setItemMeta(ihMeta);
        inv.setItem(INCREASE_HUNDRED, increaseHundred);

        ItemStack increaseFiveHundred = new ItemStack(Material.LIME_STAINED_GLASS, 1);
        ItemMeta ifhMeta = increaseFiveHundred.getItemMeta();
        ifhMeta.setDisplayName(Utils.formatText("&aIncrease by &l" + CxTokens.currency + "500"));
        increaseFiveHundred.setItemMeta(ifhMeta);
        inv.setItem(INCREASE_FIVE_HUNDRED, increaseFiveHundred);

        ItemStack maxOut = new ItemStack(Material.YELLOW_STAINED_GLASS, 1);
        ItemMeta maxMeta = maxOut.getItemMeta();
        maxMeta.setDisplayName(Utils.formatText(
                "&eSet to &e&l" + CxTokens.currency + "" + TokenPlayer.getTokenPlayer(tokens, player).getTokens()));
        maxOut.setItemMeta(maxMeta);
        inv.setItem(SET_TO_MAX, maxOut);

        player.closeInventory();
        player.openInventory(inv);
    }

    /**
     * Create a send transfer for the player. "Send Transfers" are used to keep
     * track of a player while
     * they're using different GUI menus.
     *
     * @param player The player who wants to send money
     */
    private void createTransfer(Player player) {
        if (transfers.get(player.getUniqueId()) != null) {
            // Transfer already exists
            return;
        }

        SendTransfer newSendTransfer = new SendTransfer(
                TokenPlayer.getTokenPlayer(this.tokens, player),
                null,
                0);
        transfers.put(player.getUniqueId(), newSendTransfer);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleTransferTarget(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        if (inv == null) {
            return;
        }

        String title = event.getView().getTitle();
        if (!title.contains(Utils.formatText("&a&lSelect Transfer Target"))) {
            return;
        } else {
            event.setCancelled(true);
        }

        if (inv.getHolder() != null) {
            return;
        }

        if (inv.getType() != InventoryType.CHEST) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        int clickedSlot = event.getSlot();
        // Selection title is "Select Transfer Target - [PAGE]", so just split at "- "
        // then get index by -1
        int page = Integer.valueOf(title.split("- ")[1]) - 1;
        if (clickedSlot == Utils.LARGE_EXIT_PREVIOUS_SLOT && page == 0) {
            this.transfers.remove(player.getUniqueId());
            player.closeInventory();
            return;
        }

        Player[] onlinePlayers = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        this.tokens.getLogger().info(String.valueOf(clickedSlot));
        int selectedPlayerIndex = (page * 45) + clickedSlot;
        Player selectedPlayer = onlinePlayers[selectedPlayerIndex];
        if (selectedPlayer == null) {
            this.transfers.remove(player.getUniqueId());
            player.closeInventory();
            return;
        }

        this.transfers.get(player.getUniqueId()).transferTarget = TokenPlayer.getTokenPlayer(this.tokens,
                selectedPlayer);
        openTransferAmountSelection(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleTransferAmount(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }

        if (!event.getView().getTitle().contains(Utils.formatText("&a&lSelect Transfer Amount"))) {
            return;
        } else {
            event.setCancelled(true);
        }

        if (event.getClickedInventory().getHolder() != null) {
            return;
        }

        if (event.getClickedInventory().getType() != InventoryType.CHEST) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        TokenPlayer tokenPlayer = TokenPlayer.getTokenPlayer(this.tokens, player);
        SendTransfer transfer = transfers.get(player.getUniqueId());
        int clickedSlot = event.getSlot();
        if (clickedSlot == Utils.SMALL_EXIT_PREVIOUS_SLOT || transfer == null || transfer.transferTarget == null) {
            this.transfers.remove(player.getUniqueId());
            player.closeInventory();
            this.openTransferTargetSelection(player);
            return;
        }

        if (clickedSlot == SET_TO_ZERO) {
            transfer.amount = 0;
            player.closeInventory();
            this.openTransferAmountSelection(player);
            return;
        }

        if (clickedSlot == DECREASE_FIVE_HUNDRED) {
            if (transfer.amount - 500 < 0) {
                return;
            }

            transfer.amount = transfer.amount - 500;
            player.closeInventory();
            this.openTransferAmountSelection(player);
            return;
        }

        if (clickedSlot == DECREASE_HUNDRED) {
            if (transfer.amount - 100 < 0) {
                return;
            }

            transfer.amount = transfer.amount - 100;
            player.closeInventory();
            this.openTransferAmountSelection(player);
            return;
        }

        if (clickedSlot == SEND) {
            transfer.transferTarget.transferToPlayer(tokenPlayer, transfer.amount);
            this.transfers.remove(player.getUniqueId());
            player.closeInventory();
            return;
        }

        if (clickedSlot == INCREASE_HUNDRED) {
            if (transfer.amount + 100 > tokenPlayer.getTokens()) {
                return;
            }

            transfer.amount = transfer.amount + 100;
            player.closeInventory();
            this.openTransferAmountSelection(player);
            return;
        }

        if (clickedSlot == INCREASE_FIVE_HUNDRED) {
            if (transfer.amount + 500 > tokenPlayer.getTokens()) {
                return;
            }

            transfer.amount = transfer.amount + 500;
            player.closeInventory();
            this.openTransferAmountSelection(player);
            return;
        }

        if (clickedSlot == SET_TO_MAX) {
            transfer.amount = tokenPlayer.getTokens();
            player.closeInventory();
            this.openTransferAmountSelection(player);
            return;
        }
    }
}
