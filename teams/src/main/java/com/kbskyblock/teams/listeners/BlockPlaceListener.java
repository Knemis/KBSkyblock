package com.kbskyblock.teams.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.PermissionType;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamBlock;
import com.kbskyblock.teams.database.TeamSpawners;
import com.kbskyblock.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Optional;

@AllArgsConstructor
public class BlockPlaceListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (teams.getTeamManager().isBankItem(event.getItemInHand())) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        U user = teams.getUserManager().getUser(player);
        Optional<T> team = teams.getTeamManager().getTeamViaPlayerLocation(player, event.getBlock().getLocation());

        if (team.isPresent()) {
            if (!teams.getTeamManager().getTeamPermission(team.get(), user, PermissionType.BLOCK_PLACE)) {
                player.sendMessage(StringUtils.color(teams.getMessages().cannotPlaceBlocks
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                event.setCancelled(true);
            }
        } else {
            teams.getTeamManager().handleBlockPlaceOutsideTerritory(event);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void monitorBlockPlace(BlockPlaceEvent event) {
        U user = teams.getUserManager().getUser(event.getPlayer());
        XMaterial material = XMaterial.matchXMaterial(event.getBlock().getType());
        teams.getTeamManager().getTeamViaID(user.getTeamID()).ifPresent(team -> {
            teams.getMissionManager().handleMissionUpdate(team, event.getBlock().getLocation().getWorld(), "PLACE", material.name(), 1);
        });
        teams.getTeamManager().getTeamViaPlayerLocation(event.getPlayer(), event.getBlock().getLocation()).ifPresent(team -> {
            TeamBlock teamBlock = teams.getTeamManager().getTeamBlock(team, material);
            teamBlock.setAmount(teamBlock.getAmount() + 1);

            if (event.getBlock().getState() instanceof CreatureSpawner) {
                CreatureSpawner creatureSpawner = (CreatureSpawner) event.getBlock().getState();

                if(creatureSpawner.getSpawnedType() == null) return;

                TeamSpawners teamSpawners = teams.getTeamManager().getTeamSpawners(team, creatureSpawner.getSpawnedType());
                teamSpawners.setAmount(teamSpawners.getAmount() + 1);
            }
        });
    }
}
