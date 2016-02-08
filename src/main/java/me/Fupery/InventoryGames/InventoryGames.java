package me.Fupery.InventoryGames;

import me.Fupery.InventoryGames.Commands.CommandListener;
import me.Fupery.InventoryGames.Games.Connect_Four;
import me.Fupery.InventoryGames.Games.Tic_Tac_Toe;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Reader;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryGames extends JavaPlugin {

    public static final GameFactory gameFactory = new GameFactory();
    private GameListener gameListener;

    public static void runTask(Runnable runnable) {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("InventoryGames");
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public static void runTaskAsync(Runnable runnable) {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("InventoryGames");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public static InventoryGames plugin() {
        return ((InventoryGames) Bukkit.getPluginManager().getPlugin("InventoryGames"));
    }

    public static ConcurrentHashMap<UUID, Game> getActiveGames() {
        return plugin().gameListener.games;
    }

    @Override
    public void onEnable() {

//        if (!getDataFolder().exists()) {
//            getDataFolder().mkdir();
//        }
//        if (getConfig() == null) {
//            saveDefaultConfig();
//        }
        getCommand("invgame").setExecutor(new CommandListener());
        gameFactory.registerGame("Tic_Tac_Toe", Tic_Tac_Toe.class);
        gameFactory.registerGame("Connect_Four", Connect_Four.class);
        gameListener = new GameListener(this);
    }

    @Override
    public void onDisable() {

        ConcurrentHashMap<UUID, Game> games = gameListener.games;

        for (UUID id : games.keySet()) {

            if (games.get(id) != null) {
                games.get(id).stop();
            }
        }
    }

    public Reader getTextResourceFile(String fileName) {
        return getTextResource(fileName);
    }

}
