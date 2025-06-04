package com.kbskyblock.KBSkyblock;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class KBSkyblock extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "KBSkyblock has been enabled!");
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "KBSkyblock has been disabled!");
        // Plugin shutdown logic
    }
}
