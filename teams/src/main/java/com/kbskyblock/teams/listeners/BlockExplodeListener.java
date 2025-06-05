
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
import org.bukkit.event.block.BlockExplodeEvent;

import java.util.Optional;

@AllArgsConstructor
public class BlockExplodeListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockExplode(BlockExplodeEvent event) {

        if (!teams.getConfiguration().preventTntGriefing) return;
        Optional<T> currentTeam = teams.getTeamManager().getTeamViaLocation(event.getBlock().getLocation());

        if (currentTeam.isPresent()) {
            TeamSetting teamSetting = teams.getTeamManager().getTeamSetting(currentTeam.get(), SettingType.TNT_DAMAGE.getSettingKey());
            if (teamSetting == null) return;
            if (teamSetting.getValue().equalsIgnoreCase("Disabled")) {
                event.setCancelled(true);
                return;
            }
        }

        int currentTeamId = currentTeam.map(T::getId).orElse(0);

        event.blockList().removeIf(blockState -> {
            Optional<T> team = teams.getTeamManager().getTeamViaLocation(blockState.getLocation());
            return team.map(T::getId).orElse(currentTeamId) != currentTeamId;
        });
    }

}
