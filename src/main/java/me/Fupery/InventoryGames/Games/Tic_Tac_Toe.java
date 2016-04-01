package me.Fupery.InventoryGames.Games;

import me.Fupery.InventoryGames.Game;
import me.Fupery.InventoryGames.Utils.InventoryTracer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Tic_Tac_Toe extends Game {

    public Tic_Tac_Toe() {
        inventoryTemplate = new InventoryTemplate(InventoryType.DISPENSER);
    }

    @Override
    public boolean click(InventoryClickEvent event) {

        if (!super.click(event)) {
            return false;
        }
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        Inventory inventory = event.getClickedInventory();

        if (inventory.getItem(slot) == null || inventory.getItem(slot).getType() == Material.AIR) {
            ItemStack itemStack = new ItemStack(players.getToken(player));
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName("§a§lPlaced By " + player.getName());
            itemStack.setItemMeta(meta);
            inventory.setItem(slot, itemStack);

            if (evaluateVictory(player, slot)) {
                endGame(player, players.getWaitingPlayer());

            } else {
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                players.nextTurn();
                updateTurn(inventory.getContents());
            }
        }
        return true;
    }

    @Override
    public boolean evaluateVictory(Player player, int clickedslot) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        return new InventoryTracer(inventory, players.getToken(player), 3).findChain(clickedslot);
    }
}
