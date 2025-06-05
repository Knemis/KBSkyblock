package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamInvite;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public class UnInviteCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public UnInviteCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (args.length != 1) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }
        U offlinePlayer = teams.getUserManager().getUser(Bukkit.getServer().getOfflinePlayer(args[0]));
        Optional<TeamInvite> teamInvite = teams.getTeamManager().getTeamInvite(team, offlinePlayer);
        if (!teamInvite.isPresent()) {
            player.sendMessage(StringUtils.color(teams.getMessages().noActiveInvite.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }

        teams.getTeamManager().deleteTeamInvite(teamInvite.get());
        player.sendMessage(StringUtils.color(teams.getMessages().teamInviteRevoked
                .replace("%prefix%", teams.getConfiguration().prefix)
                .replace("%player%", offlinePlayer.getName())
        ));
        
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args, KBSkyblockTeams<T, U> teams) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

}
