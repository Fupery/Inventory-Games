package me.Fupery.InventoryGames.Games;

import me.Fupery.InventoryGames.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Checkers extends Game {

    public Checkers() {
        inventoryTemplate = new InventoryTemplate(54);
    }

    @Override
    public boolean click(InventoryClickEvent event) {
        if (!super.click(event)) {
            return false;
        }
        switch (event.getAction()) {

            case PICKUP_ALL: case PICKUP_SOME: case PICKUP_HALF: case PICKUP_ONE:
                break;
            case PLACE_ALL: case PLACE_SOME: case PLACE_ONE:
                break;
            case SWAP_WITH_CURSOR:
                break;
            case DROP_ALL_CURSOR:
                break;
            case DROP_ONE_CURSOR:
                break;
            case DROP_ALL_SLOT:
                break;
            case DROP_ONE_SLOT:
                break;
            case MOVE_TO_OTHER_INVENTORY:
                break;
            case HOTBAR_MOVE_AND_READD:
                break;
            case HOTBAR_SWAP:
                break;
            case CLONE_STACK:
                break;
            case COLLECT_TO_CURSOR:
                break;
            case UNKNOWN:
                break;
        }
        return false;
    }

    @Override
    public boolean evaluateVictory(Player player, int clickedSlot) {
        return false;
    }
}
