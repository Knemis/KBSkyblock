package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.PermissionType;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class TrustCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public TrustCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (args.length != 1) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }
        if (!teams.getTeamManager().getTeamPermission(team, user, PermissionType.TRUST)) {
            player.sendMessage(StringUtils.color(teams.getMessages().cannotTrust
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        Player invitee = Bukkit.getServer().getPlayer(args[0]);
        if (invitee == null) {
            player.sendMessage(StringUtils.color(teams.getMessages().notAPlayer
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        U offlinePlayerUser = teams.getUserManager().getUser(invitee);
        if (offlinePlayerUser.getTeamID() == team.getId()) {
            player.sendMessage(StringUtils.color(teams.getMessages().userAlreadyInTeam
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        if (teams.getTeamManager().getTeamTrust(team, offlinePlayerUser).isPresent()) {
            player.sendMessage(StringUtils.color(teams.getMessages().trustAlreadyPresent
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }

        teams.getTeamManager().createTeamTrust(team, offlinePlayerUser, user);
        player.sendMessage(StringUtils.color(teams.getMessages().teamTrustSent
                .replace("%prefix%", teams.getConfiguration().prefix)
                .replace("%player%", offlinePlayerUser.getName())
        ));
        invitee.sendMessage(StringUtils.color(teams.getMessages().teamTrustReceived
                .replace("%prefix%", teams.getConfiguration().prefix)
                .replace("%player%", player.getName())
        ));
        
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args, KBSkyblockTeams<T, U> teams) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

}
