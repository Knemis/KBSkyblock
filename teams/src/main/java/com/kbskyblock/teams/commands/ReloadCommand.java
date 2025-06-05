package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.List;

@NoArgsConstructor
public class ReloadCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public ReloadCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(CommandSender sender, String[] arguments, KBSkyblockTeams<T, U> teams) {
        teams.loadConfigs();
        sender.sendMessage(StringUtils.color(teams.getMessages().reloaded.replace("%prefix%", teams.getConfiguration().prefix)));
        return true;
    }

}
