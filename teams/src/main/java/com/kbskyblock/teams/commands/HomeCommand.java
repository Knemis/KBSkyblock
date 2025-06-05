package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

@NoArgsConstructor
public class HomeCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public HomeCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        Location home = team.getHome();
        if (home == null) {
            player.sendMessage(StringUtils.color(teams.getMessages().homeNotSet
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        if (teams.getTeamManager().getTeamViaLocation(home).map(T::getId).orElse(0) != team.getId()) {
            player.sendMessage(StringUtils.color(teams.getMessages().homeNotInTeam
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        if (teams.getTeamManager().teleport(player, home, team)) {
            player.sendMessage(StringUtils.color(teams.getMessages().teleportingHome
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
        }
        return true;
    }

}
