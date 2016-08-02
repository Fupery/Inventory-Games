package me.Fupery.InventoryGames;

import me.Fupery.InventoryGames.Commands.CommandListener;
import me.Fupery.InventoryGames.GUI.MenuHandler;
import me.Fupery.InventoryGames.Games.Connect_Four;
import me.Fupery.InventoryGames.Games.Tic_Tac_Toe;
import me.Fupery.InventoryMenu.API.InventoryMenu;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryGames extends JavaPlugin {

    public static final GameFactory gameFactory = new GameFactory();
    private GameListener gameListener;
    private CommandListener commandListener;
    private MenuHandler handler;
    private YamlConfiguration data;
    private boolean saveScheduled;

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

    private static GameFactory getGameFactory() {
        return gameFactory;
    }

    private static int[] readDataTag(String string) {
        String[] split = StringUtils.split(string, ':');
        return new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1])};
    }

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(), "data.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        data = YamlConfiguration.loadConfiguration(file);

        commandListener = new CommandListener();
        handler = new MenuHandler(this);

        getCommand("invgame").setExecutor(commandListener);
        //register games here----------------------------------------
        gameFactory.registerGame("Tic_Tac_Toe", Tic_Tac_Toe.class);
        gameFactory.registerGame("Connect_Four", Connect_Four.class);
        //-----------------------------------------------------------
        gameListener = new GameListener(this);
        saveScheduled = false;
    }

    @Override
    public void onDisable() {

        ConcurrentHashMap<UUID, Game> games = gameListener.games;
        ConcurrentHashMap<UUID, InventoryMenu> openMenus = handler.getListener().getOpenMenus();

        for (UUID id : games.keySet()) {

            if (games.get(id) != null) {
                games.get(id).stop();
            }
        }

        for (UUID id : openMenus.keySet()) {

            if (openMenus.get(id) != null) {
                openMenus.get(id).close(Bukkit.getPlayer(id));
            }
        }


        try {
            data.save(new File(getDataFolder(), "data.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Reader getTextResourceFile(String fileName) {
        return getTextResource(fileName);
    }

    public CommandListener getCommandListener() {
        return commandListener;
    }

    public MenuHandler getHandler() {
        return handler;
    }

    void logPlayerStats(UUID player, String game, boolean victory) {
        ConfigurationSection gameData = getGameData(game);
        int[] winStats;
        String uuid = player.toString();
        int i = victory ? 0 : 1;

        winStats = gameData.contains(uuid) ? readDataTag(gameData.getString(uuid)) : new int[]{0, 0};
        winStats[i]++;
        gameData.set(uuid, winStats[0] + ":" + winStats[1]);
        savePlayerStats();
    }

    public String[] getPlayerStats(UUID player, String game) {
        ConfigurationSection gameData = getGameData(game);
        int[] winStats;
        String uuid = player.toString();
        String prefix = " §r➯ §6";

        winStats = gameData.contains(uuid) ? readDataTag(gameData.getString(uuid)) : new int[]{0, 0};
        return new String[]{prefix + winStats[0] + " Wins", prefix + winStats[1] + " Losses"};
    }

    private ConfigurationSection getGameData(String gameName) {
        String game = gameName.toUpperCase();
        return (data.contains(game)) ? data.getConfigurationSection(game) : data.createSection(game);
    }

    private void savePlayerStats() {
        if (!saveScheduled) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    saveScheduled = false;
                    try {
                        getConfig().save(new File(getDataFolder(), "data.yml"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 6000);//five minutes
            saveScheduled = true;
        }
    }
}
