package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.PermissionType;
import com.kbskyblock.teams.Rank;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class PromoteCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public PromoteCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (args.length != 1) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }

        OfflinePlayer targetPlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
        U targetUser = teams.getUserManager().getUser(targetPlayer);

        if (targetUser.getTeamID() != team.getId()) {
            player.sendMessage(StringUtils.color(teams.getMessages().userNotInYourTeam.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }

        int nextRank = targetUser.getUserRank() + 1;

        if (!DoesRankExist(nextRank, teams) || IsHigherRank(targetUser, user) || !teams.getTeamManager().getTeamPermission(team, user, PermissionType.PROMOTE)) {
            player.sendMessage(StringUtils.color(teams.getMessages().cannotPromoteUser.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }

        targetUser.setUserRank(nextRank);

        for (U member : teams.getTeamManager().getTeamMembers(team)) {
            Player teamMember = Bukkit.getPlayer(member.getUuid());
            if (teamMember != null) {
                if (teamMember.equals(player)) {
                    teamMember.sendMessage(StringUtils.color(teams.getMessages().promotedPlayer
                            .replace("%player%", targetUser.getName())
                            .replace("%rank%", teams.getUserRanks().get(nextRank).name)
                            .replace("%prefix%", teams.getConfiguration().prefix)
                    ));
                } else {
                    teamMember.sendMessage(StringUtils.color(teams.getMessages().userPromotedPlayer
                            .replace("%promoter%", player.getName())
                            .replace("%player%", targetUser.getName())
                            .replace("%rank%", teams.getUserRanks().get(nextRank).name)
                            .replace("%prefix%", teams.getConfiguration().prefix)
                    ));
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args, KBSkyblockTeams<T, U> teams) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

    private boolean DoesRankExist(int rank, KBSkyblockTeams<T, U> teams) {
        if (rank < 1) return false;
        return teams.getUserRanks().containsKey(rank);
    }

    private boolean IsHigherRank(U target, U user) {
        if (target.getUserRank() == Rank.OWNER.getId()) return true;
        if (user.getUserRank() == Rank.OWNER.getId()) return false;
        if (user.isBypassing()) return false;
        return target.getUserRank() + 1 >= user.getUserRank();
    }

}
