
package com.kbskyblock.teams.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

@AllArgsConstructor
public class FurnaceSmeltListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void monitorFurnaceSmelt(FurnaceSmeltEvent event) {
        XMaterial material = XMaterial.matchXMaterial(event.getSource().getType());

        teams.getTeamManager().getTeamViaLocation(event.getBlock().getLocation()).ifPresent(team -> {
            teams.getMissionManager().handleMissionUpdate(team, event.getBlock().getLocation().getWorld(), "SMELT", material.name(), 1);
        });

    }

}
