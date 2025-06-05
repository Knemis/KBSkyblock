package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.SettingType;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamInvite;
import com.kbskyblock.teams.database.TeamSetting;
import com.kbskyblock.teams.enhancements.MembersEnhancementData;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public class JoinCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public JoinCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (args.length != 1) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }
        if (teams.getTeamManager().getTeamViaID(user.getTeamID()).isPresent()) {
            player.sendMessage(StringUtils.color(teams.getMessages().alreadyHaveTeam
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        Optional<T> team = teams.getTeamManager().getTeamViaNameOrPlayer(args[0]);
        if (!team.isPresent()) {
            player.sendMessage(StringUtils.color(teams.getMessages().teamDoesntExistByName
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        TeamSetting teamSetting = teams.getTeamManager().getTeamSetting(team.get(), SettingType.TEAM_TYPE.getSettingKey());
        Optional<TeamInvite> teamInvite = teams.getTeamManager().getTeamInvite(team.get(), user);
        if (!teamInvite.isPresent() && !user.isBypassing() && teamSetting != null && !teamSetting.getValue().equalsIgnoreCase("public")) {
            player.sendMessage(StringUtils.color(teams.getMessages().noActiveInvite
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }

        MembersEnhancementData data = teams.getEnhancements().membersEnhancement.levels.get(teams.getTeamManager().getTeamEnhancement(team.get(), "members").getLevel());
        if (teams.getTeamManager().getTeamMembers(team.get()).size() >= (data == null ? 0 : data.members)) {
            player.sendMessage(StringUtils.color(teams.getMessages().memberLimitReached
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }

        user.setTeam(team.get());
        teamInvite.ifPresent(invite -> teams.getTeamManager().deleteTeamInvite(invite));

        player.sendMessage(StringUtils.color(teams.getMessages().joinedTeam
                .replace("%prefix%", teams.getConfiguration().prefix)
                .replace("%name%", team.get().getName())
        ));

        teams.getTeamManager().getTeamMembers(team.get()).stream()
                .map(U::getPlayer)
                .forEach(teamMember -> {
                    if (teamMember != null && teamMember != player) {
                        teamMember.sendMessage(StringUtils.color(teams.getMessages().userJoinedTeam
                                .replace("%prefix%", teams.getConfiguration().prefix)
                                .replace("%player%", player.getName())
                        ));
                    }
                });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args, KBSkyblockTeams<T, U> teams) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

}
