package com.kbskyblock.core.multiversion;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class CoreInventory {
    public abstract Inventory getTopInventory(Player player);
}
