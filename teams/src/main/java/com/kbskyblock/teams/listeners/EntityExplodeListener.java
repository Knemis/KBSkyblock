
package com.kbskyblock.teams.listeners;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.SettingType;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamSetting;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class EntityExplodeListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!teams.getConfiguration().preventTntGriefing) return;
        List<MetadataValue> list = event.getEntity().getMetadata("team_spawned");
        Optional<T> currentTeam = teams.getTeamManager().getTeamViaLocation(event.getEntity().getLocation());

        if (currentTeam.isPresent()) {
            TeamSetting tntDisabled = teams.getTeamManager().getTeamSetting(currentTeam.get(), SettingType.TNT_DAMAGE.getSettingKey());
            TeamSetting entityGriefDisabled = teams.getTeamManager().getTeamSetting(currentTeam.get(), SettingType.ENTITY_GRIEF.getSettingKey());

            if (tntDisabled == null && entityGriefDisabled == null) return;
            boolean isTnTDisabled = tntDisabled != null && tntDisabled.getValue().equalsIgnoreCase("Disabled");
            boolean isEntityGriefDisabled = entityGriefDisabled != null && entityGriefDisabled.getValue().equalsIgnoreCase("Disabled");
            if (isTnTDisabled || isEntityGriefDisabled) {
                event.setCancelled(true);
                return;
            }
        }

        int originalTeamId = list.stream().map(MetadataValue::asInt).findFirst().orElse(0);

        event.blockList().removeIf(blockState -> {
            Optional<T> team = teams.getTeamManager().getTeamViaLocation(blockState.getLocation());
            return team.map(T::getId).orElse(originalTeamId) != originalTeamId;
        });
    }

}
