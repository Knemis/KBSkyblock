package com.kbskyblock.teams.support;

import com.earth2me.essentials.Essentials;
import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class EssentialsSpawnSupport<T extends Team, U extends KBSkyblockUser<T>> implements SpawnSupport<T> {

    private final KBSkyblockTeams<T, U> teams;

    // EssentialsSpawn essentialsSpawn = (EssentialsSpawn) Bukkit.getPluginManager().getPlugin("EssentialsSpawn"); // Commented out
    Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

    public EssentialsSpawnSupport(KBSkyblockTeams<T, U> teams) {
        this.teams = teams;
    }

    @Override
    public String supportProvider() {
        // return essentialsSpawn.getName(); // Commented out
        return essentials != null ? essentials.getName() : "Essentials (Spawn module not found)"; // Adjusted
    }

    @Override
    public Location getSpawn(Player player) {
        // if (essentialsSpawn != null && essentials != null) return essentialsSpawn.getSpawn(essentials.getUser(player).getGroup()); // Commented out
        if (essentials != null) {
            // Fallback or alternative logic if only core Essentials is available but not EssentialsSpawn specific API
            // This might just be the server's main spawn or Essentials' default spawn
            try {
                // Location spawn = essentials.getSpawn().getSpawn(essentials.getUser(player).getGroup()); // Commented out
                // if (spawn != null) return spawn; // Commented out
            } catch (Exception e) {
                // Essentials might not have a spawn configured for the group, or API usage is incorrect for just core
            }
        }
        return Bukkit.getWorlds().get(0).getSpawnLocation(); // Default fallback
    }
}
