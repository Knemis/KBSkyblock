package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class FlyCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {

    @Getter
    String flyAnywherePermission;

    public FlyCommand(List<String> args, String description, String syntax, String permission, String flyAnywherePermission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
        this.flyAnywherePermission = flyAnywherePermission;
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();

        boolean flight = !user.isFlying();
        if (args.length == 1) {
            if (!args[0].equalsIgnoreCase("enable") && !args[0].equalsIgnoreCase("disable") && !args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off")) {
                player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
                return false;
            }

            flight = args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("on");
        }

        if (!canFly(player, teams)) {
            player.sendMessage(StringUtils.color(teams.getMessages().flightNotActive.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }

        user.setFlying(flight);
        player.setAllowFlight(flight);
        player.setFlying(flight);

        if (flight) {
            player.sendMessage(StringUtils.color(teams.getMessages().flightEnabled.replace("%prefix%", teams.getConfiguration().prefix)));
        } else {
            player.sendMessage(StringUtils.color(teams.getMessages().flightDisabled.replace("%prefix%", teams.getConfiguration().prefix)));
        }
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, KBSkyblockTeams<T, U> teams) {
        return true;
    }

    public boolean canFly(Player player, KBSkyblockTeams<T, U> teams) {
        U user = teams.getUserManager().getUser(player);
        return user.canFly(teams);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args, KBSkyblockTeams<T, U> teams) {
        return Arrays.asList("enable", "disable", "on", "off");
    }
}
