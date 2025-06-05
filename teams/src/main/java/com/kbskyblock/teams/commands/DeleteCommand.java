package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.Rank;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class DeleteCommand<T extends Team, U extends KBSkyblockUser<T>> extends ConfirmableCommand<T, U> {
    public String adminPermission;

    public DeleteCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds, String adminPermission, boolean requiresConfirmation) {
        super(args, description, syntax, permission, cooldownInSeconds, requiresConfirmation);
        this.adminPermission = adminPermission;
    }

    @Override
    public boolean execute(U user, String[] arguments, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (arguments.length == 1) {
            if (!player.hasPermission(adminPermission)) {
                player.sendMessage(StringUtils.color(teams.getMessages().noPermission
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                return false;
            }

            Optional<T> team = teams.getTeamManager().getTeamViaNameOrPlayer(arguments[0]);
            if (!team.isPresent()) {
                player.sendMessage(StringUtils.color(teams.getMessages().teamDoesntExistByName
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                return false;
            }
            return execute(user, team.get(), arguments, teams);
        }
        return super.execute(user, arguments, teams);
    }

    @Override
    protected boolean isCommandValid(U user, T team, String[] arguments, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (arguments.length == 1) {
            return true;
        }

        if (user.getUserRank() != Rank.OWNER.getId() && !user.isBypassing()) {
            player.sendMessage(StringUtils.color(teams.getMessages().cannotDeleteTeam
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        return true;
    }

    @Override
    protected void executeAfterConfirmation(U user, T team, String[] arguments, KBSkyblockTeams<T, U> teams) {
        if (arguments.length == 1) {
            deleteTeam(user, team, teams, true);
        }

        deleteTeam(user, team, teams, false);
    }

    private void deleteTeam(U user, T team, KBSkyblockTeams<T, U> teams, boolean admin) {
        Player player = user.getPlayer();
        if (!teams.getTeamManager().deleteTeam(team, user)) return;

        for (U member : teams.getTeamManager().getTeamMembers(team)) {
            member.setTeamID(0);
            Player teamMember = member.getPlayer();
            if (teamMember != null) {
                teamMember.sendMessage(StringUtils.color(teams.getMessages().teamDeleted
                        .replace("%prefix%", teams.getConfiguration().prefix)
                        .replace("%player%", player.getName())
                ));
            }
        }
        if (admin) {
            player.sendMessage(StringUtils.color(teams.getMessages().deletedPlayerTeam
                    .replace("%prefix%", teams.getConfiguration().prefix)
                    .replace("%name%", team.getName())
            ));
        }
    }

}
