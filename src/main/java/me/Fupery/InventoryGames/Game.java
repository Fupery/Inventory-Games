package me.Fupery.InventoryGames;

import me.Fupery.InventoryGames.Utils.PlayerPair;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Game {

    protected PlayerPair players;
    protected boolean running = false;
    protected boolean updatingMenu = false;
    protected InventoryTemplate inventoryTemplate;

    public boolean start() {

        if (players == null) {
            return false;
        }
        updateTurn(null);
        running = true;
        return true;
    }

    public void addPlayers(UUID player1, UUID player2) {
        players = PlayerPair.generateTokenPair(player1, player2);
    }

    public void stop() {

        running = false;
        ConcurrentHashMap<UUID, Game> games = InventoryGames.getActiveGames();

        for (PlayerPair.GamePlayer player : players) {
            games.remove(player.getPlayer());
            Bukkit.getPlayer(player.getPlayer()).closeInventory();
        }
    }

    public void update() {
        for (PlayerPair.GamePlayer player : players) {
            Bukkit.getPlayer(player.getPlayer()).updateInventory();
        }
    }

    public void updateTurn(ItemStack[] contents) {
        String title = players.getCurrentPlayer().getName() + "'s turn";
        final Inventory inventory = inventoryTemplate.build(title);

        if (contents != null) {
            inventory.setContents(contents);
        }
        updatingMenu = true;
        InventoryGames.runTask(new Runnable() {
            @Override
            public void run() {

                for (PlayerPair.GamePlayer player : players) {
                    Bukkit.getPlayer(player.getPlayer()).openInventory(inventory);
                }
                updatingMenu = false;
            }
        });
    }

    public boolean click(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!running) {
            return false;
        }

        if (!players.isPlayersTurn(player)) {
            return false;
        }
        if (players == null) {
            player.sendMessage("Not enough players!");
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
            return false;
        }
        return true;
    }

    public abstract boolean evaluateVictory(Player player, int clickedSlot);

    public PlayerPair getPlayers() {
        return players;
    }

    public boolean isUpdating() {
        return updatingMenu;
    }

    protected class InventoryTemplate {
        private InventoryType type;
        private int inventorySize;

        public InventoryTemplate(InventoryType type) {
            this.type = type;
            inventorySize = 0;
        }

        public InventoryTemplate(int inventorySize) {
            this.inventorySize = inventorySize;
            type = null;
        }

        public Inventory build(String title) {
            return (type == null) ? Bukkit.createInventory(null, inventorySize, title)
                    : Bukkit.createInventory(null, type, title);
        }
    }
}