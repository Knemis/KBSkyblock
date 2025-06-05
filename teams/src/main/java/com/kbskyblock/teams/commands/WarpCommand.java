package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamWarp;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public class WarpCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public WarpCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (args.length != 1 && args.length != 2) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }
        Optional<TeamWarp> teamWarp = teams.getTeamManager().getTeamWarp(team, args[0]);
        if (!teamWarp.isPresent()) {
            player.sendMessage(StringUtils.color(teams.getMessages().unknownWarp
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        if (teamWarp.get().getPassword() != null) {
            if (args.length != 2 || !teamWarp.get().getPassword().equals(args[1])) {
                player.sendMessage(StringUtils.color(teams.getMessages().incorrectPassword
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                return false;
            }
        }
        
        if (teams.getTeamManager().teleport(player, teamWarp.get().getLocation(), team)) {
            player.sendMessage(StringUtils.color(teams.getMessages().teleportingWarp
                    .replace("%prefix%", teams.getConfiguration().prefix)
                    .replace("%name%", teamWarp.get().getName())
            ));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        List<TeamWarp> teamWarps = teams.getTeamManager().getTeamWarps(team);
        return teamWarps.stream().map(TeamWarp::getName).collect(Collectors.toList());
    }
}
