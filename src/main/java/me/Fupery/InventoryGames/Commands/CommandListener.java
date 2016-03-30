package me.Fupery.InventoryGames.Commands;

import me.Fupery.InventoryGames.GUI.PlayerMenu;
import me.Fupery.InventoryGames.InventoryGames;
import me.Fupery.InventoryGames.Utils.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CommandListener implements CommandExecutor {

    private final ConcurrentHashMap<String, AbstractCommand> commands = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, GameRequest> pendingGames = new ConcurrentHashMap<>();

    public CommandListener() {

        commands.put("help", new AbstractCommand(null, "/invgame [help]", true) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                sender.sendMessage(Lang.Array.HELP.messages());
            }
        });
        commands.put("list", new AbstractCommand(null, "/invgame list", true) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                sender.sendMessage(Lang.GAME_LIST.message() + ChatColor.RESET
                        + String.valueOf(InventoryGames.gameFactory.getGameList()));
            }
        });
        commands.put("play", new AbstractCommand("inventorygames.user", "/invgame play <game> [player]", false) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

                if (!InventoryGames.gameFactory.gameExists(args[1])) {
                    sender.sendMessage(String.format(Lang.GAME_NOT_FOUND.message(), args[1]));
                    return;
                }
                final Player target;

                if (args.length > 2) {
                    target = Bukkit.getPlayer(args[2]);
                    if (target == null) {
                        sender.sendMessage(String.format(Lang.PLAYER_NOT_FOUND.message(), args[2]));
                        return;
                    }
                } else {
                    PlayerMenu.openMenu((Player) sender, args[1]);
                    return;
                }
                processGameRequest((Player) sender, target, args[1]);
            }
        });
        commands.put("accept", new AbstractCommand(null, "/invgame accept", false) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                UUID player = ((Player) sender).getUniqueId();

                if (pendingGames.containsKey(player)) {
                    pendingGames.get(player).startGame();
                    pendingGames.remove(player);

                } else {
                    sender.sendMessage(Lang.NO_REQUESTS.message());
                }
            }
        });
    }

    public void processGameRequest(Player player1, final Player player2, String gameName) {
        if (pendingGames.containsKey(player2.getUniqueId())) {

            if (pendingGames.get(player2.getUniqueId()).getRequester().equals(player1.getUniqueId())) {
                player1.sendMessage(String.format(Lang.ALREADY_REQUESTED.message(), player2.getName()));
                return;
            }
        }

        if (pendingGames.containsKey(player1.getUniqueId())) {
            GameRequest request = pendingGames.get(player1.getUniqueId());

            if (request.getRequester().equals(player2.getUniqueId())) {
                request.startGame();
                return;
            }
        }

        BukkitRunnable timeout = new BukkitRunnable() {
            @Override
            public void run() {
                if (pendingGames.containsKey(player2.getUniqueId())) {
                    pendingGames.remove(player2.getUniqueId());
                }
            }
        };
        pendingGames.put(player2.getUniqueId(),
                new GameRequest(player1, player2, gameName, timeout));
        player1.sendMessage(String.format(Lang.REQUEST_SENT.message(), player2.getName(), gameName));
        timeout.runTaskLaterAsynchronously(InventoryGames.plugin(), 600);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length > 0) {

            if (commands.containsKey(args[0].toLowerCase())) {
                commands.get(args[0].toLowerCase()).runPlayerCommand(sender, args);

            } else {
                sender.sendMessage(Lang.COMMAND_HELP.message());
            }

        } else {
            commands.get("help").runPlayerCommand(sender, args);
        }
        return true;
    }
}
