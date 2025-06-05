package com.kbskyblock.core.multiversion;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CoreInventory_V1_16_R2 extends CoreInventory {
    @Override
    public Inventory getTopInventory(Player player) {
        return player.getOpenInventory().getTopInventory();
    }
}
