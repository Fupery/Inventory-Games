package me.Fupery.InventoryGames.GUI;

import me.Fupery.InventoryMenu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

public class MenuHandler extends Menu {

    public MenuHandler(JavaPlugin plugin) {
        super(plugin, null, InventoryType.HOPPER);
    }

    @Override
    public void open(JavaPlugin plugin, Player player) { }
}
