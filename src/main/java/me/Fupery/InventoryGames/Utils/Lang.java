package me.Fupery.InventoryGames.Utils;

import me.Fupery.InventoryGames.InventoryGames;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public enum Lang {
    NO_PERMISSION(true), NO_CONSOLE(true), GAME_NOT_FOUND(true), PLAYER_NOT_FOUND(true), REQUEST_SENT(false),
    ALREADY_REQUESTED(true), REQUEST(false), NO_REQUESTS(true), GAME_LIST(false), COMMAND_HELP(true), WINNER(false),
    INVITE(false), CHOOSE_PLAYER(false);
    public static final String prefix = "§e[InvGames] ";
    final boolean isErrorMessage;
    String message;

    Lang(boolean isErrorMessage) {
        this.isErrorMessage = isErrorMessage;
        InventoryGames plugin = JavaPlugin.getPlugin(InventoryGames.class);
//        String language = plugin.getConfig().getString("language");
        String language = "english";
        FileConfiguration langFile =
                YamlConfiguration.loadConfiguration(plugin.getTextResourceFile("lang.yml"));

        if (!langFile.contains(language)) {
            language = "english";
        }
        ConfigurationSection lang = langFile.getConfigurationSection(language);

        if (lang.get(name()) != null) {
            message = lang.getString(name());

        } else {
            Bukkit.getLogger().warning(String.format("%sError loading %s from Lang.yml", prefix, name()));
        }
    }

    public String message() {
        ChatColor colour = (isErrorMessage) ? ChatColor.RED : ChatColor.GOLD;
        return prefix + colour + message;
    }

    public String rawMessage() {
        return message;
    }

    public enum Array {
        HELP;

        String[] messages;

        Array() {
            InventoryGames plugin = JavaPlugin.getPlugin(InventoryGames.class);
//            String language = plugin.getConfig().getString("language");
            String language = "english";
            FileConfiguration langFile =
                    YamlConfiguration.loadConfiguration(plugin.getTextResourceFile("lang.yml"));

            if (!langFile.contains(language)) {
                language = "english";
            }
            ConfigurationSection lang = langFile.getConfigurationSection(language);

            if (lang.get(name()) != null) {
                List<String> strings = lang.getStringList(name());
                messages = strings.toArray(new String[strings.size()]);

            } else {
                Bukkit.getLogger().warning(String.format("Error loading %s from Lang.yml", name()));
            }
        }

        public String[] messages() {
            return messages;
        }
    }
}