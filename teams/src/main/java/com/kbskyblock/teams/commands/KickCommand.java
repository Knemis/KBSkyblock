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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public class KickCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public KickCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (args.length != 1) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }
        if (!teams.getTeamManager().getTeamPermission(team, user, PermissionType.KICK)) {
            player.sendMessage(StringUtils.color(teams.getMessages().cannotKick
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
        U kickedPlayer = teams.getUserManager().getUser(offlinePlayer);
        if (team.getId() != kickedPlayer.getTeamID()) {
            player.sendMessage(StringUtils.color(teams.getMessages().userNotInYourTeam
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        if (offlinePlayer.getUniqueId() == player.getUniqueId()) {
            player.sendMessage(StringUtils.color(teams.getMessages().cannotKickYourself
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        if ((kickedPlayer.getUserRank() >= user.getUserRank() || kickedPlayer.getUserRank() == Rank.OWNER.getId()) && !user.isBypassing() && user.getUserRank() != Rank.OWNER.getId()) {
            player.sendMessage(StringUtils.color(teams.getMessages().cannotKickHigherRank
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        kickedPlayer.setTeam(null);
        Optional.ofNullable(kickedPlayer.getPlayer()).ifPresent(player1 -> player1.sendMessage(StringUtils.color(teams.getMessages().youHaveBeenKicked
                .replace("%prefix%", teams.getConfiguration().prefix)
                .replace("%player%", player.getName())
        )));
        teams.getTeamManager().getTeamMembers(team).stream().map(U::getPlayer).filter(Objects::nonNull).forEach(player1 ->
                player1.sendMessage(StringUtils.color(teams.getMessages().playerKicked
                        .replace("%prefix%", teams.getConfiguration().prefix)
                        .replace("%player%", kickedPlayer.getName())
                        .replace("%kicker%", player.getName())
                ))
        );
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args, KBSkyblockTeams<T, U> teams) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

}
