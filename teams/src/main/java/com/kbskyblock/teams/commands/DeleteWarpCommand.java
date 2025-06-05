package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.PermissionType;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamWarp;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public class DeleteWarpCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public DeleteWarpCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (args.length != 1 && args.length != 2) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }
        if (!teams.getTeamManager().getTeamPermission(team, user, PermissionType.MANAGE_WARPS)) {
            player.sendMessage(StringUtils.color(teams.getMessages().cannotManageWarps
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }

        Optional<TeamWarp> teamWarp = teams.getTeamManager().getTeamWarp(team, args[0]);
        if (!teamWarp.isPresent()) {
            player.sendMessage(StringUtils.color(teams.getMessages().unknownWarp
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }

        teams.getTeamManager().deleteWarp(teamWarp.get());
        teams.getTeamManager().getTeamMembers(team).stream().map(U::getPlayer).filter(Objects::nonNull).forEach(member ->
                member.sendMessage(StringUtils.color(teams.getMessages().deletedWarp
                        .replace("%prefix%", teams.getConfiguration().prefix)
                        .replace("%player%", player.getName())
                        .replace("%name%", teamWarp.get().getName())
                ))
        );
        return true;
    }

    @Override
    public List<String> onTabComplete(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        List<TeamWarp> teamWarps = teams.getTeamManager().getTeamWarps(team);
        return teamWarps.stream().map(TeamWarp::getName).collect(Collectors.toList());
    }
}
