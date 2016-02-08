package me.Fupery.InventoryGames.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

public class PlayerPair implements Iterable<PlayerPair.GamePlayer> {

    boolean turn = false;
    private GamePlayer player1;
    private GamePlayer player2;

    PlayerPair(GamePlayer player1, GamePlayer player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

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

    public Player getCurrentPlayer() {
        return turn ? Bukkit.getPlayer(player1.getPlayer()) : Bukkit.getPlayer(player2.getPlayer());
    }

    public Player getWaitingPlayer() {
        return !turn ? Bukkit.getPlayer(player1.getPlayer()) : Bukkit.getPlayer(player2.getPlayer());
    }

    public boolean isPlayersTurn(Player player) {
        GamePlayer currentPlayer = turn ? player1 : player2;
        return currentPlayer.getPlayer().equals(player.getUniqueId());
    }

    public void sendMessage(String message) {
        Bukkit.getPlayer(player1.getPlayer()).sendMessage(message);
        Bukkit.getPlayer(player2.getPlayer()).sendMessage(message);
    }

    public void playSound(Sound sound) {
        Player player1 = Bukkit.getPlayer(this.player1.getPlayer());
        Player player2 = Bukkit.getPlayer(this.player2.getPlayer());
        player1.playSound(player1.getLocation(), sound, 1, 1);
        player2.playSound(player2.getLocation(), sound, 1, 1);
    }

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
    }

}
