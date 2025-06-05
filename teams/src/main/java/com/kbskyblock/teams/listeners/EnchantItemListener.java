
package com.kbskyblock.teams.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

@AllArgsConstructor
public class EnchantItemListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void monitorItemEnchant(EnchantItemEvent event) {
        U user = teams.getUserManager().getUser(event.getEnchanter());
        XMaterial material = XMaterial.matchXMaterial(event.getItem().getType());
        teams.getTeamManager().getTeamViaID(user.getTeamID()).ifPresent(team -> {
            teams.getMissionManager().handleMissionUpdate(team, event.getEnchanter().getLocation().getWorld(), "ENCHANT", material.name(), 1);
        });

    }

}
