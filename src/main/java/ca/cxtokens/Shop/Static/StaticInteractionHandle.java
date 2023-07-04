package ca.cxtokens.Shop.Static;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import ca.cxtokens.TokenPlayer;
import ca.cxtokens.Utils;

public class StaticInteractionHandle implements Listener {
    /*
     * Double chests have 54 slots (D.Bs are used for the store)
     * 45 = bottom left slot
     * 53 = bottom right slot
     */
    private final int BACK_EXIT = 45;
    private final int NEXT_PAGE = 53;

    private void purchaseItem(Item item, Player player) {
        TokenPlayer tokenPlayer = TokenPlayer.convertPlayerToTokenPlayer(player);
        if (tokenPlayer.getTokens() < item.price) {
            player.sendMessage(Utils.formatText("&cYou don't have enough tokens to purchase this item!"));
            return;
        }
        
        tokenPlayer.subtractTokens(item.price, false);
        player.getInventory().addItem(new ItemStack(item.stack.getType(), item.stack.getAmount()));
    }

    private void sellItem(Item item, Player player) {
        TokenPlayer tokenPlayer = TokenPlayer.convertPlayerToTokenPlayer(player);
        if (!player.getInventory().contains(item.stack.getType())) {
            player.sendMessage(Utils.formatText("&cYou has to have this item in your inventory to sell!"));
            return;
        }
        
        int i = 0;
        for (ItemStack it : player.getInventory().getContents()) {
            if (it != null && it.getType() == item.stack.getType()) {
                if (it.getAmount() < item.stack.getAmount()) {
                    player.sendMessage(Utils.formatText("&cYou must have at least " + item.stack.getAmount() + " to sell this item!"));
                    return;
                }
                break;
            }
            i++;
        }

        int sellPrice = Math.round(item.price * item.sellMultiplier);
        tokenPlayer.addTokens(sellPrice, false);
        int currAmount = player.getInventory().getItem(i).getAmount();
        player.getInventory().getItem(i).setAmount(currAmount - item.stack.getAmount());
    }
    
    @EventHandler
    public void onStoreClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) {
            return;
        }
        if (e.getClickedInventory().getHolder() != null) {
            return;
        }
        if (e.getClickedInventory().getType() != InventoryType.CHEST) {
            return;
        }
        if (!e.getView().getTitle().contains("Store")) {
            return;
        }

        e.setCancelled(true);

        int currentPage = Integer.parseInt(e.getView().getTitle().split(" ")[3]) - 1;
        int clicked = e.getSlot();

        if (clicked == BACK_EXIT) {
            e.getView().close();

            // Exit
            if (currentPage == 0) {
                return;
            }

            // Go Back
            Store.openStaticStorePage(TokenPlayer.convertPlayerToTokenPlayer((Player) e.getWhoClicked()), (currentPage - 1));
            return;
        }

        if (clicked == NEXT_PAGE) {
            e.getView().close();
            Store.openStaticStorePage(TokenPlayer.convertPlayerToTokenPlayer((Player) e.getWhoClicked()), (currentPage + 1));
            return;
        }

        if (clicked > Pages.pages[currentPage].length) {
            return;
        }
        if (currentPage > Pages.pages.length) {
            return;
        }

        Item item = Pages.pages[currentPage][clicked];

        if (e.getClick().isLeftClick()) {
            purchaseItem(item, (Player) e.getWhoClicked());
            return;
        }
        sellItem(item, (Player) e.getWhoClicked());
    }
}