package me.Fupery.InventoryGames.Utils;

import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerPair implements Iterable<PlayerPair.GamePlayer> {

    boolean turn = false;
    private GamePlayer player1;
    private GamePlayer player2;

    PlayerPair(GamePlayer player1, GamePlayer player2) {
        this.player1 = player1;
        this.player2 = player2;
        ThreadLocalRandom.current().nextBoolean();
    }

    /**
     * @param player1
     * @param player2
     * @return A new player pair with randomly assigned item tokens to represent their moves in-game.
     */
    public static PlayerPair generateTokenPair(UUID player1, UUID player2) {

        Material[] materials;

        int rand = new Random().nextInt(7);
        switch (rand) {
            case 1:
                materials = new Material[]{Material.CAKE, Material.COOKIE};
                break;
            case 2:
                materials = new Material[]{Material.COMPASS, Material.WATCH};
                break;
            case 3:
                materials = new Material[]{Material.REDSTONE, Material.GLOWSTONE_DUST};
                break;
            case 4:
                materials = new Material[]{Material.COOKED_BEEF, Material.GRILLED_PORK};
                break;
            case 5:
                materials = new Material[]{Material.GOLDEN_CARROT, Material.SPECKLED_MELON};
                break;
            case 6:
                materials = new Material[]{Material.BLAZE_POWDER, Material.MAGMA_CREAM};
                break;
            default:
                materials = new Material[]{Material.DIAMOND, Material.EMERALD};
        }

        GamePlayer pt1 = new GamePlayer(player1, materials[0]);
        GamePlayer pt2 = new GamePlayer(player2, materials[1]);

        return new PlayerPair(pt1, pt2);
    }

    @Override
    public Iterator<GamePlayer> iterator() {
        return new TokenIterator(this);
    }

    /**
     * @return The player whose turn it is
     */
    public Player getCurrentPlayer() {
        return turn ? Bukkit.getPlayer(player1.getPlayer()) : Bukkit.getPlayer(player2.getPlayer());
    }

    /**
     * @return The player whose turn it isn't
     */
    public Player getWaitingPlayer() {
        return !turn ? Bukkit.getPlayer(player1.getPlayer()) : Bukkit.getPlayer(player2.getPlayer());
    }

    /**
     * @param player
     * @return Check if it is currently this players' turn
     */
    public boolean isPlayersTurn(Player player) {
        GamePlayer currentPlayer = turn ? player1 : player2;
        return currentPlayer.getPlayer().equals(player.getUniqueId());
    }

    /**
     * @param message Sends a message ot both players
     */
    public void sendMessage(String message) {
        Bukkit.getPlayer(player1.getPlayer()).sendMessage(message);
        Bukkit.getPlayer(player2.getPlayer()).sendMessage(message);
    }

    /**
     * @param sound Plays a sound for both players
     */
    public void playSound(SoundCompat sound) {
        Player player1 = Bukkit.getPlayer(this.player1.getPlayer());
        Player player2 = Bukkit.getPlayer(this.player2.getPlayer());
        sound.play(player1);
        sound.play(player2);
    }

    /**
     * Advances the current turn, giving it to the waiting player
     */
    public void nextTurn() {
        turn = !turn;
    }

    public Player getPlayer(Material material) {

        for (GamePlayer token : this) {

            if (token.getToken() == material) {
                return Bukkit.getPlayer(token.getPlayer());
            }
        }
        return null;
    }

    /**
     * @param player The player
     * @return The player's token item, representing their place on the board
     */
    public Material getToken(Player player) {

        for (GamePlayer token : this) {

            if (token.getPlayer().equals(player.getUniqueId())) {
                return token.token;
            }
        }
        return Material.AIR;
    }

    public Player getPlayer1() {
        return Bukkit.getPlayer(player1.player);
    }

    public Player getPlayer2() {
        return Bukkit.getPlayer(player2.player);
    }

    /**
     * Represents a player in a game
     */
    public static class GamePlayer {

        private UUID player;
        private Material token;

        private GamePlayer(UUID player, Material token) {
            this.player = player;
            this.token = token;
        }

        public UUID getPlayer() {
            return player;
        }

        public Material getToken() {
            return token;
        }
    }

    private class TokenIterator implements Iterator<GamePlayer> {

        private int i = 0;
        private PlayerPair playerPair;

        TokenIterator(PlayerPair playerPair) {
            this.playerPair = playerPair;
        }

        @Override
        public boolean hasNext() {
            return i <= 1;
        }

        @Override
        public GamePlayer next() {
            GamePlayer player = (i == 0 ? playerPair.player1 : playerPair.player2);
            i++;
            return player;
        }

        @Override
        public void remove() {
            //do nothing
        }
    }

}
