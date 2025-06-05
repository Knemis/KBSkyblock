package com.kbskyblock.teams.listeners;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.PermissionType;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Optional;

@AllArgsConstructor
public class EntityDamageListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) return;
        Player player = (Player) damager;
        U user = teams.getUserManager().getUser(player);
        Optional<T> team = teams.getTeamManager().getTeamViaLocation(event.getEntity().getLocation());
        if (team.isPresent()) {
            if (!teams.getTeamManager().getTeamPermission(team.get(), user, PermissionType.KILL_MOBS)) {
                player.sendMessage(StringUtils.color(teams.getMessages().cannotKillMobs
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                event.setCancelled(true);
            }
        }
    }
}
