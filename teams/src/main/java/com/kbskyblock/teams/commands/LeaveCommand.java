package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.Rank;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

@NoArgsConstructor
public class LeaveCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public LeaveCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();

        if (user.getUserRank() == Rank.OWNER.getId()) {
            player.sendMessage(StringUtils.color(teams.getMessages().ownerCannotLeave
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }

        player.sendMessage(StringUtils.color(teams.getMessages().leftTeam
                .replace("%prefix%", teams.getConfiguration().prefix)
                .replace("%name%", team.getName())
        ));

        teams.getTeamManager().getTeamMembers(team).forEach(teamUser -> {
            Player teamPlayer = Bukkit.getPlayer(teamUser.getUuid());
            if (teamPlayer != null && teamPlayer != player) {
                teamPlayer.sendMessage(StringUtils.color(teams.getMessages().userLeftTeam
                        .replace("%prefix%", teams.getConfiguration().prefix)
                        .replace("%name%", team.getName())
                        .replace("%player%", player.getName())
                ));
            }
        });

        user.setTeam(null);
        return true;
    }

}
