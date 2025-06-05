package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
public class AboutCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {

    public AboutCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(CommandSender sender, String[] arguments, KBSkyblockTeams<T, U> teams) {
        sender.sendMessage(StringUtils.color("&7Plugin Name: " + teams.getCommandManager().getColor() + teams.getDescription().getName()));
        sender.sendMessage(StringUtils.color("&7Plugin Version: " + teams.getCommandManager().getColor() + teams.getDescription().getVersion()));
        sender.sendMessage(StringUtils.color("&7Plugin Author: " + teams.getCommandManager().getColor() + "Peaches_MLG"));
        sender.sendMessage(StringUtils.color("&7Plugin Donations: " + teams.getCommandManager().getColor() + "www.patreon.com/Peaches_MLG"));

        HashSet<String> providerList = teams.getSupportManager().getProviderList();
        if(!providerList.isEmpty())
            sender.sendMessage(StringUtils.color("&7Detected Plugins Supported: " + teams.getCommandManager().getColor() + String.join(", ", providerList)));

        return true;
    }

}
