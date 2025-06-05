
package com.kbskyblock.teams.listeners;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

@AllArgsConstructor
public class EntityDeathListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void monitorEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if(killer==null)return;
        U user = teams.getUserManager().getUser(killer);
        teams.getTeamManager().getTeamViaID(user.getTeamID()).ifPresent(team -> {
            teams.getMissionManager().handleMissionUpdate(team, killer.getLocation().getWorld(), "KILL", event.getEntityType().name(), 1);
        });

    }

}
