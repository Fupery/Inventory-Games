package me.Fupery.InventoryGames.Games;

import me.Fupery.InventoryGames.Game;
import me.Fupery.InventoryGames.InventoryGames;
import me.Fupery.InventoryGames.Utils.InventoryTracer;
import me.Fupery.InventoryGames.Utils.Lang;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Connect_Four extends Game {

    private boolean dropping = false;

    public Connect_Four() {
        inventoryTemplate = new InventoryTemplate(54);
    }

    @Override
    public boolean click(InventoryClickEvent event) {

        if (dropping) {
            return false;
        }

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
            dropping = true;
            new FallingItemAnimation(itemStack, inventory, slot).start();
        }
        return true;
    }

    @Override
    public boolean evaluateVictory(Player player, int clickedSlot) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        return new InventoryTracer(inventory, players.getToken(player), 4).findChain(clickedSlot);
    }

    class FallingItemAnimation extends BukkitRunnable {

        ItemStack item;
        Inventory inventory;
        int increment;
        int size;
        int currentSlot;
        int previousSlot;

        public FallingItemAnimation(ItemStack item, Inventory inventory, int clickedSlot) {
            this.item = item;
            this.inventory = inventory;
            previousSlot = -1;
            currentSlot = clickedSlot;
            size = inventory.getSize();
            increment = 9;
        }

        void start() {
            runTaskTimer(InventoryGames.plugin(), 0, 1);
        }

        @Override
        public void run() {

            if (currentSlot < size && (inventory.getItem(currentSlot) == null
                    || inventory.getItem(currentSlot).getType() == Material.AIR)) {
                inventory.setItem(currentSlot, item);
                if (previousSlot >= 0) {
                    inventory.setItem(previousSlot, new ItemStack(Material.AIR));
                }
                update();
                previousSlot = currentSlot;
                currentSlot += increment;

            } else {

                cancel();

                Player currentPlayer = players.getCurrentPlayer();

                if (evaluateVictory(currentPlayer, previousSlot)) {
                    Player loser = players.getWaitingPlayer();
                    currentPlayer.playSound(currentPlayer.getLocation(), Sound.LEVEL_UP, 1, 1);
                    loser.playSound(loser.getLocation(), Sound.FIZZ, 1, 1);
                    players.sendMessage(String.format(Lang.WINNER.message(), currentPlayer.getName()));
                    running = false;
                    update();

                } else {
                    players.playSound(Sound.WOOD_CLICK);
                    players.nextTurn();
                    updateTurn(inventory.getContents());
                }
                dropping = false;
            }
        }
    }
}
