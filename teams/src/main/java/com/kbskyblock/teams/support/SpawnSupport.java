package com.kbskyblock.teams.support;

import com.kbskyblock.teams.database.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface SpawnSupport<T extends Team> {
    public Location getSpawn(Player player);
    public String supportProvider();
}