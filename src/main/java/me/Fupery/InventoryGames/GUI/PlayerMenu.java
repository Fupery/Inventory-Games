package me.Fupery.InventoryGames.GUI;

import me.Fupery.InventoryGames.InventoryGames;
import me.Fupery.InventoryGames.Utils.Lang;
import me.Fupery.InventoryMenu.API.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerMenu extends ListMenu {

    private String gameName;
    private UUID owner;

    private PlayerMenu(Player player, String gameName) {
        super(InventoryGames.plugin().getHandler(), Lang.CHOOSE_PLAYER.rawMessage());
        this.gameName = gameName;
        this.owner = player.getUniqueId();
    }

    @Override
    public MenuButton[] generateListButtons() {
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        final List<MenuButton> buttons = new ArrayList<>();

        boolean foundPlayer = false;
        int i = 0;
        for (Player player : players) {
            if (!foundPlayer && player.getUniqueId().equals(owner)) {
                foundPlayer = true;
                continue;
            }
            buttons.add(new InviteButton(player));
        }
        return buttons.toArray(new MenuButton[buttons.size()]);
    }

    private class InviteButton extends MenuButton {

        private UUID target;

        InviteButton(Player player) {
            super(Material.SKULL_ITEM, player.getName(), Lang.INVITE.rawMessage());

            setDurability((short) 3);
            SkullMeta meta = (SkullMeta) getItemMeta();

            meta.setOwner(player.getName());
            setItemMeta(meta);
            this.target = player.getUniqueId();
        }

        @Override
        public void onClick(JavaPlugin javaPlugin, Player player) {
            Player playerToInvite = Bukkit.getPlayer(target);
            if (playerToInvite != null && playerToInvite.isOnline()) {
                InventoryGames.plugin().getCommandListener().processGameRequest(
                        player, Bukkit.getPlayer(target), gameName);
                player.closeInventory();
            }
        }
    }
    public static void openMenu(Player player, String gameName) {
        new PlayerMenu(player, gameName).open(InventoryGames.plugin(), player);
    }
}
