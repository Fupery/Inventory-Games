package me.Fupery.InventoryGames;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameListener implements Listener {

    public final ConcurrentHashMap<UUID, Game> games;
    private final JavaPlugin plugin;

    public GameListener(JavaPlugin plugin) {
        this.plugin = plugin;
        games = new ConcurrentHashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    protected void handleClick(InventoryClickEvent event) {
        Inventory top = event.getWhoClicked().getOpenInventory().getTopInventory();
        Inventory bottom = event.getWhoClicked().getOpenInventory().getBottomInventory();

        if (event.getClickedInventory() == top) {
            event.setResult(Event.Result.DENY);
            event.setCancelled(true);

        } else if (event.getClickedInventory() == bottom) {

            switch (event.getAction()) {
                case MOVE_TO_OTHER_INVENTORY:
                case HOTBAR_MOVE_AND_READD:
                case COLLECT_TO_CURSOR:
                case UNKNOWN:
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                    return;
                default:
                    break;
            }
        }
    }

    @EventHandler
    public void onMenuInteract(final InventoryClickEvent event) {

        if (!games.containsKey(event.getWhoClicked().getUniqueId())) {
            return;
        }

        handleClick(event);

        final Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() != player.getOpenInventory().getTopInventory()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Game game = games.get(player.getUniqueId());
                game.click(event);
            }
        });
    }

    @EventHandler
    public void onItemDrag(InventoryDragEvent event) {

        if (!games.containsKey(event.getWhoClicked().getUniqueId())) {
            return;
        }
        event.setResult(Event.Result.DENY);
        event.setCancelled(true);
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent event) {
        Player player = ((Player) event.getPlayer());

        if (player != null && games.containsKey(player.getUniqueId())) {
            Game game = games.get(player.getUniqueId());

            if (!game.isUpdating()) {
                game.stop();
            }
        }
    }
}
