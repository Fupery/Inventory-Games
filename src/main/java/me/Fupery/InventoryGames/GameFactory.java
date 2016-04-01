package me.Fupery.InventoryGames;

import java.util.HashMap;
import java.util.Set;

public class GameFactory {

    private final HashMap<String, Class<? extends Game>> games = new HashMap<>();

    public Game buildGame(String gameName) {

        Class<? extends Game> gameClass = games.get(gameName.toUpperCase());

        try {
            Game game = gameClass.newInstance();
            game.setName(gameName.toUpperCase());
            return game;

        } catch (NullPointerException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void registerGame(String gameName, Class<? extends Game> game) {
        games.put(gameName.toUpperCase(), game);
    }

    public Set<String> getGameList() {
        return games.keySet();
    }

    public boolean gameExists(String gameName) {
        return games.containsKey(gameName.toUpperCase());
    }
}
