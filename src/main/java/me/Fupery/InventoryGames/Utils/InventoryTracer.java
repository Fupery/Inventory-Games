package me.Fupery.InventoryGames.Utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Iterator;

public class InventoryTracer {

    private final int width;
    private final Material chainItem;
    private final int chainLength;
    private final Inventory inventory;

    public InventoryTracer(Inventory inventory, Material chainItem, int chainLength) {
        this.inventory = inventory;
        this.chainLength = chainLength;
        this.chainItem = chainItem;
        width = getInventoryWidth(inventory.getType());
    }

    public static int getInventoryWidth(InventoryType type) {
        switch (type) {
            case DISPENSER:
            case DROPPER:
            case WORKBENCH:
                return 3;
            case CRAFTING:
                return 2;
            case CHEST:
                return 9;
            case HOPPER:
                return 5;
            default:
                return 0;
        }
    }

    public boolean findChain(int clickedSlot) {

        for (Direction direction : Direction.values()) {
            ItemTracer tracer = new ItemTracer(direction, clickedSlot, chainLength);
            HashMap<Integer, ItemStack> items = new HashMap<>();
            items.put(clickedSlot, inventory.getItem(clickedSlot));

            while (tracer.hasNext()) {
                ItemStack item = tracer.next();

                if (item != null) {
                    items.put(tracer.currentSlot, item);
                }
            }
            if (tracer.itemsToFind == 0) {

                for (Integer slot : items.keySet()) {
                    ItemStack item = items.get(slot);
                    item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 1);
                    ItemMeta meta = item.getItemMeta();
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    item.setItemMeta(meta);
                    inventory.setItem(slot, item);
                }
                return true;
            }
        }
        return false;
    }

    enum Direction {
        LEFT(-1, 0), LEFT_UP(-1, -1), UP(0, -1), RIGHT_UP(1, -1);

        final int x, y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    class ItemTracer implements Iterator<ItemStack> {

        int x, y;
        boolean flipped;
        private int itemsToFind;
        private int currentSlot;
        private int initialSlot;

        private ItemTracer(Direction direction, int currentSlot, int itemsToFind) {
            this.itemsToFind = itemsToFind - 1;
            this.currentSlot = currentSlot;
            this.initialSlot = currentSlot;
            flipped = false;
            this.x = direction.x;
            this.y = direction.y;
        }

        @Override
        public boolean hasNext() {

            if (checkNextSlotInvalid()) {

                if (!flipped && itemsToFind > 0) {
                    flipDirection();
                    return !checkNextSlotInvalid();

                } else {
                    return false;
                }
            }
            return true;
        }

        private boolean checkNextSlotInvalid() {
            int relativePos = currentSlot;

            while (relativePos >= width && relativePos > 0) {
                relativePos -= width;
            }
            if ((y < 0 && currentSlot < width)
                    || (y > 0 && currentSlot >= inventory.getSize() - width)
                    || (x < 0 && relativePos == 0)
                    || (x > 0 && relativePos == width - 1)) {
                return true;
            }
            ItemStack nextItem = inventory.getItem(currentSlot + x + (y * width));
            return (nextItem == null || nextItem.getType() != chainItem);
        }

        @Override
        public ItemStack next() {
            currentSlot += x + (y * width);

            if (inventory.getItem(currentSlot) != null
                    && inventory.getItem(currentSlot).getType() == chainItem) {
                itemsToFind--;
                return inventory.getItem(currentSlot);
            }
            return null;
        }

        private void flipDirection() {
            x = -x;
            y = -y;
            currentSlot = initialSlot;
            flipped = true;
        }
    }
}
