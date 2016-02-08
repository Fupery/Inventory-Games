package me.Fupery.InventoryGames.Commands;

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
        commands.put("play", new AbstractCommand(null, "/invgame play <game> <player>", false) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

                if (!InventoryGames.gameFactory.gameExists(args[1])) {
                    sender.sendMessage(String.format(Lang.GAME_NOT_FOUND.message(), args[1]));
                    return;
                }
                final Player target = Bukkit.getPlayer(args[2]);

                if (target == null) {
                    sender.sendMessage(String.format(Lang.PLAYER_NOT_FOUND.message(), args[2]));
                    return;
                }

                Player player = (Player) sender;

                if (pendingGames.containsKey(target.getUniqueId())) {

                    if (pendingGames.get(target.getUniqueId()).getRequester().equals(player.getUniqueId())) {
                        player.sendMessage(String.format(Lang.ALREADY_REQUESTED.message(), args[2]));
                        return;
                    }
                }

                if (pendingGames.containsKey(player.getUniqueId())) {
                    GameRequest request = pendingGames.get(player.getUniqueId());

                    if (request.getRequester().equals(target.getUniqueId())) {
                        request.startGame();
                        return;
                    }
                }

                BukkitRunnable timeout = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (pendingGames.containsKey(target.getUniqueId())) {
                            pendingGames.remove(target.getUniqueId());
                        }
                    }
                };
                pendingGames.put(target.getUniqueId(),
                        new GameRequest(((Player) sender), target, args[1], timeout));
                sender.sendMessage(String.format(Lang.REQUEST_SENT.message(), args[2], args[1]));
                timeout.runTaskLaterAsynchronously(InventoryGames.plugin(), 600);
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
