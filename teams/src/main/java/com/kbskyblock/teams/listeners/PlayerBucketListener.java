
package com.kbskyblock.teams.listeners;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.PermissionType;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import java.util.Optional;

@AllArgsConstructor
public class PlayerBucketListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler(ignoreCancelled = true)
    public void onBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        onBucketEvent(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketFillEvent(PlayerBucketFillEvent event) {
        onBucketEvent(event);
    }

    public void onBucketEvent(PlayerBucketEvent event) {
        Player player = event.getPlayer();
        U user = teams.getUserManager().getUser(player);
        Optional<T> team = teams.getTeamManager().getTeamViaPlayerLocation(player, event.getBlock().getLocation());
        if (team.isPresent()) {
            if (!teams.getTeamManager().getTeamPermission(team.get(), user, PermissionType.BUCKET)) {
                player.sendMessage(StringUtils.color(teams.getMessages().cannotUseBuckets
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                event.setCancelled(true);
            }
        }

    }

}
