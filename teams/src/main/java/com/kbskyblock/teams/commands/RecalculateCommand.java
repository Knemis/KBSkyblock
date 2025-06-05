package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@NoArgsConstructor
public class RecalculateCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public RecalculateCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(CommandSender sender, String[] arguments, KBSkyblockTeams<T, U> teams) {
        if (teams.isRecalculating()) {
            sender.sendMessage(StringUtils.color(teams.getMessages().calculationAlreadyInProcess
                    .replace("%prefix%", teams.getConfiguration().prefix))
            );
            return false;
        }

        int interval = teams.getConfiguration().forceRecalculateInterval;
        List<T> teams = teams.getTeamManager().getTeams();
        int seconds = (teams.size() * interval / 20) % 60;
        int minutes = (teams.size() * interval / 20) / 60;
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (!player.hasPermission(permission)) continue;
            player.sendMessage(StringUtils.color(teams.getMessages().calculatingTeams
                    .replace("%prefix%", teams.getConfiguration().prefix)
                    .replace("%player%", sender.getName())
                    .replace("%minutes%", String.valueOf(minutes))
                    .replace("%seconds%", String.valueOf(seconds))
                    .replace("%amount%", String.valueOf(teams.size()))
            ));
        }
        teams.setRecalculating(true);
        return true;
    }

}
