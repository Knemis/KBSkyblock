package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.Placeholder;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public class LevelCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {

    public LevelCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (args.length == 0) {
            Optional<T> userTeam = teams.getTeamManager().getTeamViaID(user.getTeamID());
            if (!userTeam.isPresent()) {
                player.sendMessage(StringUtils.color(teams.getMessages().dontHaveTeam
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                return false;
            }
            sendTeamLevel(player, userTeam.get(), teams);
            return true;
        }

        Optional<T> team = teams.getTeamManager().getTeamViaNameOrPlayer(String.join(" ", args));
        if(args[0].equals("location")) {
            team = teams.getTeamManager().getTeamViaPlayerLocation(player);
        }

        if (!team.isPresent()) {
            player.sendMessage(StringUtils.color(teams.getMessages().teamDoesntExistByName
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }

        sendTeamLevel(player, team.get(), teams);
        return true;
    }

    public void sendTeamLevel(Player player, T team, KBSkyblockTeams<T, U> teams) {
        List<Placeholder> placeholderList = teams.getTeamsPlaceholderBuilder().getPlaceholders(team);
        player.sendMessage(StringUtils.color(StringUtils.getCenteredMessage(StringUtils.processMultiplePlaceholders(teams.getConfiguration().teamInfoTitle, placeholderList), teams.getConfiguration().teamInfoTitleFiller)));
        for (String line : teams.getConfiguration().levelInfo) {
            player.sendMessage(StringUtils.color(StringUtils.processMultiplePlaceholders(line, placeholderList)));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args, KBSkyblockTeams<T, U> teams) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }
}