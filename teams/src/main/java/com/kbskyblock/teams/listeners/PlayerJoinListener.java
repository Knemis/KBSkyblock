package com.kbskyblock.teams.listeners;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@AllArgsConstructor
public class PlayerJoinListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        U user = teams.getUserManager().getUser(player);
        user.setBypassing(false);
        user.initBukkitTask(teams);

        // Update the internal username in case of name change
        user.setName(event.getPlayer().getName());


        if (player.isOp() && teams.getConfiguration().patreonMessage) {
            Bukkit.getScheduler().runTaskLater(teams, () ->
                            player.sendMessage(StringUtils.color(teams.getConfiguration().prefix + " &7Thanks for using " + teams.getDescription().getName() + ", if you like the plugin, consider donating at " + teams.getCommandManager().getColor() + "www.patreon.com/Peaches_MLG"))
                    , 5);
        }

        // This isnt great, but as this requires database operations, we can pre-run it async, otherwise it will have to be loaded sync. I need to recode/rethink this eventually but this should fix some lag caused by missions for now
        teams.getTeamManager().getTeamViaID(user.getTeamID()).ifPresent(team -> Bukkit.getScheduler().runTaskAsynchronously(teams, () -> teams.getMissionManager().generateMissionData(team)));
    }

}
