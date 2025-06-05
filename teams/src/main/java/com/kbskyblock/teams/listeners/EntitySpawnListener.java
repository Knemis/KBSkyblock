
package com.kbskyblock.teams.listeners;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.SettingType;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamSetting;
import lombok.AllArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Optional;

@AllArgsConstructor
public class EntitySpawnListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler(ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Optional<T> currentTeam = teams.getTeamManager().getTeamViaLocation(event.getEntity().getLocation());
        if (currentTeam.isPresent()) {
            TeamSetting teamSetting = teams.getTeamManager().getTeamSetting(currentTeam.get(), SettingType.MOB_SPAWNING.getSettingKey());
            if (teamSetting == null) return;
            if (teamSetting.getValue().equalsIgnoreCase("Disabled") && event.getEntity() instanceof LivingEntity && event.getEntityType() != EntityType.ARMOR_STAND) {
                event.setCancelled(true);
            }
        }

        event.getEntity().setMetadata("team_spawned", new FixedMetadataValue(teams, currentTeam.map(T::getId).orElse(0)));
    }


}
