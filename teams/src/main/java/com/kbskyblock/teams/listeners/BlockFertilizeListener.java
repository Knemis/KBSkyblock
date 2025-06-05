
package com.kbskyblock.teams.listeners;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.PermissionType;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFertilizeEvent;

import java.util.Optional;

@AllArgsConstructor
public class BlockFertilizeListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockFertilize(BlockFertilizeEvent event) {
        Player player = event.getPlayer();

        Optional<T> currentTeam = teams.getTeamManager().getTeamViaLocation(event.getBlock().getLocation());
        int currentTeamId = currentTeam.map(T::getId).orElse(0);

        if (player != null && currentTeam.isPresent()) {
            U user = teams.getUserManager().getUser(player);
            if (!teams.getTeamManager().getTeamPermission(currentTeam.get(), user, PermissionType.BLOCK_PLACE)) {
                player.sendMessage(StringUtils.color(teams.getMessages().cannotBreakBlocks
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                event.setCancelled(true);
                return;
            }
        }

        event.getBlocks().removeIf(blockState -> {
            Optional<T> team = teams.getTeamManager().getTeamViaLocation(blockState.getLocation());
            return team.map(T::getId).orElse(currentTeamId) != currentTeamId;
        });
    }

}
