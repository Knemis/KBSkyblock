package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.PermissionType;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor

public class SetHomeCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public SetHomeCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (teams.getTeamManager().getTeamViaLocation(player.getLocation()).map(T::getId).orElse(0) != team.getId()) {
            player.sendMessage(StringUtils.color(teams.getMessages().notInTeamLand
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        if (!teams.getTeamManager().getTeamPermission(team, user, PermissionType.SETHOME)) {
            player.sendMessage(StringUtils.color(teams.getMessages().cannotSetHome
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        team.setHome(player.getLocation());
        teams.getTeamManager().getTeamMembers(team).stream().map(U::getPlayer).filter(Objects::nonNull).forEach(member ->
                member.sendMessage(StringUtils.color(teams.getMessages().homeSet
                        .replace("%prefix%", teams.getConfiguration().prefix)
                        .replace("%player%", player.getName())
                ))
        );
        return true;
    }

}
