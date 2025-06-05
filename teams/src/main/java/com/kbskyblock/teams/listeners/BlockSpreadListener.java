package com.kbskyblock.teams.listeners;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.SettingType;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamSetting;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

import java.util.Optional;

@AllArgsConstructor
public class BlockSpreadListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler(ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent event) {
        int currentTeam = teams.getTeamManager().getTeamViaLocation(event.getSource().getLocation()).map(T::getId).orElse(0);
        Optional<T> team = teams.getTeamManager().getTeamViaLocation(event.getBlock().getLocation());
        if (team.map(T::getId).orElse(currentTeam) != currentTeam) {
            event.setCancelled(true);
        }
        if(team.isPresent() && event.getSource().getType() == Material.FIRE){
            TeamSetting teamSetting = teams.getTeamManager().getTeamSetting(team.get(), SettingType.FIRE_SPREAD.getSettingKey());
            if (teamSetting == null) return;
            if (teamSetting.getValue().equalsIgnoreCase("Disabled")) {
                event.setCancelled(true);
            }
        }
    }

}