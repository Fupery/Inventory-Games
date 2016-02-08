package me.Fupery.InventoryGames.Commands;

import me.Fupery.InventoryGames.InventoryGames;
import me.Fupery.InventoryGames.Utils.Lang;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class AbstractCommand {

    private final String permission;
    private final boolean consoleAllowed;
    String usage;
    private int minArgs;
    private int maxArgs;

    AbstractCommand(String permission, String usage, boolean consoleAllowed) {
        this.permission = permission;
        this.consoleAllowed = consoleAllowed;

        if (usage == null) {
            throw new IllegalArgumentException("Usage must not be null");
        }
        this.usage = usage;
        String[] args = usage.replace("/invgame ", "").split("\\s+");
        maxArgs = args.length;
        minArgs = maxArgs - StringUtils.countMatches(usage, "[");
    }

    void runPlayerCommand(final CommandSender sender, final String args[]) {

        InventoryGames.runTaskAsync(new Runnable() {
            @Override
            public void run() {
                ReturnMessage returnMsg = new ReturnMessage(sender, null);

                if (permission != null && !sender.hasPermission(permission)) {
                    returnMsg.message = Lang.NO_PERMISSION.message();

                } else if (!consoleAllowed && !(sender instanceof Player)) {
                    returnMsg.message = Lang.NO_CONSOLE.message();

                } else if (args.length < minArgs || args.length > maxArgs) {
                    returnMsg.message = Lang.prefix + ChatColor.RED + ", " + usage;

                } else {
                    runCommand(sender, args, returnMsg);
                }

                if (returnMsg.message != null) {
                    InventoryGames.runTask(returnMsg);
                }
            }
        });
    }

    public abstract void runCommand(CommandSender sender, String[] args, ReturnMessage msg);
}

