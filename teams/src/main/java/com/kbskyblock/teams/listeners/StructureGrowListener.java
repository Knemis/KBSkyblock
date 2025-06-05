package com.kbskyblock.teams.listeners;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.Optional;

@AllArgsConstructor
public class StructureGrowListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler(ignoreCancelled = true)
    public void onBlockSpread(StructureGrowEvent event) {
        int currentTeam = teams.getTeamManager().getTeamViaLocation(event.getLocation()).map(T::getId).orElse(0);
        event.getBlocks().removeIf(blockState -> {
            Optional<T> team = teams.getTeamManager().getTeamViaLocation(blockState.getLocation());
            return team.map(T::getId).orElse(currentTeam) != currentTeam;
        });
    }

}