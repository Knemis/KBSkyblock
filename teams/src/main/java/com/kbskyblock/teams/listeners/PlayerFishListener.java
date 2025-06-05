
package com.kbskyblock.teams.listeners;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

@AllArgsConstructor
public class PlayerFishListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void monitorPlayerFish(PlayerFishEvent event) {
        Entity caughtEntity = event.getCaught();
        if (caughtEntity == null || event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        U user = teams.getUserManager().getUser(event.getPlayer());

        teams.getTeamManager().getTeamViaID(user.getTeamID()).ifPresent(team -> {
            teams.getMissionManager().handleMissionUpdate(team, caughtEntity.getLocation().getWorld(), "FISH", ((Item) caughtEntity).getItemStack().getType().name(), 1);
        });

    }

}
