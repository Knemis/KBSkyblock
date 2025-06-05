package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.Rank;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;

@NoArgsConstructor
public class CreateCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public CreateCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (teams.getTeamManager().getTeamViaID(user.getTeamID()).isPresent()) {
            player.sendMessage(StringUtils.color(teams.getMessages().alreadyHaveTeam
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }

        if (args.length < 1) {
            if (teams.getConfiguration().createRequiresName) {
                player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
                return false;
            }
            teams.getTeamManager().createTeam(player, null).thenAccept(team -> {
                if (team == null) return;
                user.setUserRank(Rank.OWNER.getId());
                player.sendMessage(StringUtils.color(teams.getMessages().teamCreated
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                getCooldownProvider().applyCooldown(player);
            });
            return false;
        }

        String teamName = String.join(" ", args);
        if (teamName.length() < teams.getConfiguration().minTeamNameLength) {
            player.sendMessage(StringUtils.color(teams.getMessages().teamNameTooShort
                    .replace("%prefix%", teams.getConfiguration().prefix)
                    .replace("%min_length%", String.valueOf(teams.getConfiguration().minTeamNameLength))
            ));
            return false;
        }
        if (teamName.length() > teams.getConfiguration().maxTeamNameLength) {
            player.sendMessage(StringUtils.color(teams.getMessages().teamNameTooLong
                    .replace("%prefix%", teams.getConfiguration().prefix)
                    .replace("%max_length%", String.valueOf(teams.getConfiguration().maxTeamNameLength))
            ));
            return false;
        }
        if (teams.getTeamManager().getTeamViaName(teamName).isPresent()) {
            player.sendMessage(StringUtils.color(teams.getMessages().teamNameAlreadyExists
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        teams.getTeamManager().createTeam(player, teamName).thenAccept(team -> {
            if (team == null) return;
            user.setUserRank(Rank.OWNER.getId());
            player.sendMessage(StringUtils.color(teams.getMessages().teamCreated
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            getCooldownProvider().applyCooldown(player);
        });
        return false;
    }

}
