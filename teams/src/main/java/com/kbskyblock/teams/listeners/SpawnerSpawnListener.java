package com.kbskyblock.teams.listeners;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamEnhancement;
import com.kbskyblock.teams.enhancements.Enhancement;
import com.kbskyblock.teams.enhancements.SpawnerEnhancementData;
import lombok.AllArgsConstructor;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

@AllArgsConstructor
public class SpawnerSpawnListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawn(SpawnerSpawnEvent event) {
        teams.getTeamManager().getTeamViaLocation(event.getLocation()).ifPresent(team -> {
            Enhancement<SpawnerEnhancementData> spawnerEnhancement = teams.getEnhancements().spawnerEnhancement;
            TeamEnhancement teamEnhancement = teams.getTeamManager().getTeamEnhancement(team, "spawner");
            SpawnerEnhancementData data = spawnerEnhancement.levels.get(teamEnhancement.getLevel());
            CreatureSpawner spawner = event.getSpawner();

            if (!teamEnhancement.isActive(spawnerEnhancement.type)) return;
            if (data == null) return;

            spawner.setSpawnCount((spawner.getSpawnCount() * data.spawnMultiplier) + data.spawnCount);
            spawner.update(true);
        });
    }
}
