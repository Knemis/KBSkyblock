package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.PermissionType;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.enhancements.WarpsEnhancementData;
import com.kbskyblock.teams.utils.LocationUtils;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;

@NoArgsConstructor
public class SetWarpCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public SetWarpCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (args.length != 1 && args.length != 2) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }
        if (!LocationUtils.isSafe(player.getLocation(), teams)) {
            player.sendMessage(StringUtils.color(teams.getMessages().notSafe
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        if (teams.getTeamManager().getTeamViaLocation(player.getLocation()).map(T::getId).orElse(0) != team.getId()) {
            player.sendMessage(StringUtils.color(teams.getMessages().notInTeamLand
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        if (!teams.getTeamManager().getTeamPermission(team, user, PermissionType.MANAGE_WARPS)) {
            player.sendMessage(StringUtils.color(teams.getMessages().cannotManageWarps
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }

        WarpsEnhancementData data = teams.getEnhancements().warpsEnhancement.levels.get(teams.getTeamManager().getTeamEnhancement(team, "warps").getLevel());
        if (teams.getTeamManager().getTeamWarps(team).size() >= (data == null ? 0 : data.warps)) {
            player.sendMessage(StringUtils.color(teams.getMessages().warpLimitReached
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }

        if (teams.getTeamManager().getTeamWarp(team, args[0]).isPresent()) {
            player.sendMessage(StringUtils.color(teams.getMessages().warpAlreadyExists
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }

        teams.getTeamManager().createWarp(team, player.getUniqueId(), player.getLocation(), args[0], args.length == 2 ? args[1] : null);
        player.sendMessage(StringUtils.color(teams.getMessages().createdWarp
                .replace("%prefix%", teams.getConfiguration().prefix)
                .replace("%name%", args[0])
        ));

        return true;
    }

}
