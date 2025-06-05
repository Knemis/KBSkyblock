package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.UserRank;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.gui.PermissionsGUI;
import com.kbskyblock.teams.gui.RanksGUI;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
public class PermissionsCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public PermissionsCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (args.length == 0) {
            player.openInventory(new RanksGUI<>(team, player, teams).getInventory());
            return false;
        }
        String rank = args[0];
        for (Map.Entry<Integer, UserRank> userRank : teams.getUserRanks().entrySet()) {
            if (!userRank.getValue().name.equalsIgnoreCase(rank)) continue;
            player.openInventory(new PermissionsGUI<>(team, userRank.getKey(), player, teams).getInventory());
            return true;
        }
        player.sendMessage(StringUtils.color(teams.getMessages().invalidUserRank.replace("%prefix%", teams.getConfiguration().prefix)));
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args, KBSkyblockTeams<T, U> teams) {
        return teams.getUserRanks().values().stream().map(userRank -> userRank.name).collect(Collectors.toList());
    }

}
