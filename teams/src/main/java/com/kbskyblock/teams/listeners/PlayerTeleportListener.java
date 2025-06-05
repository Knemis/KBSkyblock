package com.kbskyblock.teams.listeners;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Optional;

@AllArgsConstructor
public class PlayerTeleportListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();
        if (to == null) return; // This is possible apparently?
        U user = teams.getUserManager().getUser(player);
        Optional<T> toTeam = teams.getTeamManager().getTeamViaPlayerLocation(player, to);
        Optional<T> fromTeam = teams.getTeamManager().getTeamViaPlayerLocation(player, from);
        if (user.isFlying() && (to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ()) && !user.canFly(teams)) {
            user.setFlying(false);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.sendMessage(StringUtils.color(teams.getMessages().flightDisabled
                    .replace("%prefix%", teams.getConfiguration().prefix))
            );
        }
        if (!toTeam.isPresent()) return;
        if (!teams.getTeamManager().canVisit(player, toTeam.get())) {
            event.setCancelled(true);
            player.sendMessage(StringUtils.color(teams.getMessages().cannotVisit
                    .replace("%prefix%", teams.getConfiguration().prefix))
            );
            return;
        }

        if (!toTeam.map(T::getId).orElse(-1).equals(fromTeam.map(T::getId).orElse(-1))) {
            teams.getTeamManager().sendTeamTitle(player, toTeam.get());
        }
    }

}
