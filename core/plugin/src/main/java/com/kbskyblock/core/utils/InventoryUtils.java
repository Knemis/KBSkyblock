package com.kbskyblock.core.utils;

import com.cryptomorin.xseries.XMaterial;
import com.kbskyblock.core.Background;
import com.kbskyblock.core.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Various utils which perform operations on {@link Inventory}'s.
 */
public class InventoryUtils {

    /**
     * Counts the amount of the specified Material in the Inventory.
     *
     * @param inventory The inventory which should be searched
     * @param material  The material which should be counted
     * @return The amount of the material in the inventory
     */
    public static int getAmount(Inventory inventory, XMaterial material) {
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory cannot be null");
        }
        if (material == null) {
            throw new IllegalArgumentException("Material cannot be null");
        }
        int total = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item == null) continue;

            if (material.isSimilar(item)) {
                total += item.getAmount();
            }
        }
        return total;
    }

    /**
     * Removes the amount of the specified Material from the Inventory.
     *
     * @param inventory The inventory where the items should be taken from
     * @param material  The material which should be removed
     * @param amount    The amount of items of the specified material which should be removed
     */
    public static void removeAmount(Inventory inventory, XMaterial material, int amount) {
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory cannot be null");
        }
        if (material == null) {
            throw new IllegalArgumentException("Material cannot be null");
        }
        if (amount <= 0) {
            return;
        }
        int removedSoFar = 0;
        ItemStack[] contents = inventory.getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack itemStack = contents[i];
            if (itemStack == null) {
                continue;
            }
            if (removedSoFar >= amount) {
                break;
            }
            if (material.isSimilar(itemStack)) {
                int stackAmount = itemStack.getAmount();
                int amountToTakeFromStack = amount - removedSoFar;
                if (stackAmount <= amountToTakeFromStack) { // amount - removedSoFar yerine amountToTakeFromStack kullanılabilir
                    inventory.setItem(i, null);
                    removedSoFar += stackAmount;
                } else {
                    itemStack.setAmount(stackAmount - amountToTakeFromStack);
                    inventory.setItem(i, itemStack);
                    removedSoFar += amountToTakeFromStack; // DÜZELTİLDİ: += olmalı
                }
            }
        }
    }

    /**
     * Checks if the Inventory has an empty slot where ItemStacks of any type can be stored.
     *
     * @param inventory The Inventory which should be checked
     * @return true if there is at least one empty slot
     */
    public static boolean hasEmptySlot(Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory cannot be null");
        }
        return inventory.firstEmpty() != -1;
    }

    /**
     * Fills the provided Inventory with the filler material.
     * Replaces existing items. No placeholders are applied.
     *
     * @param inventory The inventory which should be filled
     * @param background The background configuration
     */
    public static void fillInventory(Inventory inventory, Background background) {
        fillInventory(inventory, background, Collections.emptyList());
    }

    /**
     * Fills the provided Inventory with the filler material and applies placeholders.
     * Replaces existing items.
     *
     * @param inventory    The inventory which should be filled
     * @param background   The background configuration
     * @param placeholders Placeholders to apply to the items
     */
    public static void fillInventory(Inventory inventory, Background background, List<Placeholder> placeholders) {
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory cannot be null");
        }
        if (background == null) {
            throw new IllegalArgumentException("Background cannot be null");
        }
        if (placeholders == null) {
            placeholders = Collections.emptyList();
        }

        // Background sınıfındaki 'filler' alanının 'com.kbskyblock.core.Item' tipinde olduğunu varsayıyoruz.
        // Eğer public değilse ve getter metodu varsa (örn: background.getFillerItem()), onu kullanın.
        Item fillerMaterial = background.filler; // VEYA background.getFiller(); (eğer Item döndürüyorsa)
        // VEYA (Item) background.getFiller(); (eğer Object döndürüp cast gerekiyorsa)


        for (int i = 0; i < inventory.getSize(); i++) {
            // ItemStackUtils.makeItem'ın (Item, List<Placeholder>) imzasını beklediğini varsayıyoruz
            inventory.setItem(i, ItemStackUtils.makeItem(fillerMaterial, placeholders));
        }

        // Background sınıfındaki 'items' map'inin Map<Integer, com.kbskyblock.core.Item> tipinde olduğunu varsayıyoruz.
        // Eğer public değilse ve getter metodu varsa (örn: background.getSpecificItemsMap()), onu kullanın.
        Map<Integer, Item> specificItems = background.items; // VEYA background.getItems(); (eğer Map<Integer, Item> döndürüyorsa)
        // VEYA cast gerekebilir.

        if (specificItems != null) {
            for (Map.Entry<Integer, Item> entry : specificItems.entrySet()) {
                int slot = entry.getKey();
                Item itemMaterial = entry.getValue(); // Artık Item tipinde

                if (slot >= 0 && slot < inventory.getSize()) {
                    inventory.setItem(slot, ItemStackUtils.makeItem(itemMaterial, placeholders));
                } else {
                    System.err.println("Uyarı: Background tanımında " + slot +
                            " slotu için bir öğe tanımlanmış, ancak bu envanter boyutu (" +
                            inventory.getSize() + ") için sınırların dışında.");
                }
            }
        }
    }
    // ItemStackUtils ve Placeholder sınıflarının projenizde doğru tanımlandığından emin olun.
    // Örnek:
    // package com.kbskyblock.core;
    // public class Item { /* ... alanlar ve metotlar ... */ }
    //
    // package com.kbskyblock.core.utils;
    // public class ItemStackUtils {
    //     public static ItemStack makeItem(com.kbskyblock.core.Item itemDefinition, List<Placeholder> placeholders) {
    //         // ... implementasyon ...
    //         if (itemDefinition == null) return null; // veya XMaterial.AIR.parseItem();
    //         // ...
    //         return new ItemStack(XMaterial.STONE.parseMaterial()); // Örnek
    //     }
    // }
    //
    // package com.kbskyblock.core.Placeholder; // veya com.kbskyblock.core.utils.Placeholder
    // public interface Placeholder { /* ... metotlar ... */ }
    //
    // package com.kbskyblock.core;
    // public class Background {
    //     public Item filler; // com.kbskyblock.core.Item tipinde
    //     public Map<Integer, Item> items; // Map<Integer, com.kbskyblock.core.Item> tipinde
    //     // Veya getter metotları:
    //     // public Item getFiller() { return filler; }
    //     // public Map<Integer, Item> getItems() { return items; }
    // }
} // InventoryUtils sınıfının kapanış parantezi