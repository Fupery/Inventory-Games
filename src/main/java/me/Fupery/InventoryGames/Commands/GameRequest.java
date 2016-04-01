package me.Fupery.InventoryGames.Commands;

import me.Fupery.InventoryGames.Game;
import me.Fupery.InventoryGames.InventoryGames;
import me.Fupery.InventoryGames.Utils.Lang;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class GameRequest {

    private final UUID requester;
    private final UUID target;
    private final String gameName;
    private final BukkitRunnable timeout;

    public GameRequest(Player requester, Player target, String gameName, BukkitRunnable timeout) {
        this.requester = requester.getUniqueId();
        this.target = target.getUniqueId();
        this.gameName = gameName;
        this.timeout = timeout;
        target.sendMessage(String.format(Lang.REQUEST.message(), requester.getName(), gameName));
        SoundCompat.DING.play(target);
    }

    public Game startGame() {
        timeout.cancel();
        Game game = InventoryGames.gameFactory.buildGame(gameName);
        game.addPlayers(requester, target);
        InventoryGames.getActiveGames().put(requester, game);
        InventoryGames.getActiveGames().put(target, game);
        game.start();
        return game;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Player
                && ((Player) obj).getUniqueId().equals(target));
    }

    public UUID getRequester() {
        return requester;
    }
}
