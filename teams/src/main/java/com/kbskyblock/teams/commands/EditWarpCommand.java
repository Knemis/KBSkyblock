package com.kbskyblock.teams.commands;

import com.cryptomorin.xseries.XMaterial;
import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.PermissionType;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamWarp;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public class EditWarpCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public EditWarpCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (args.length < 2) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }
        if (!teams.getTeamManager().getTeamPermission(team, user, PermissionType.MANAGE_WARPS)) {
            player.sendMessage(StringUtils.color(teams.getMessages().cannotManageWarps
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        Optional<TeamWarp> teamWarp = teams.getTeamManager().getTeamWarp(team, args[0]);
        if (!teamWarp.isPresent()) {
            player.sendMessage(StringUtils.color(teams.getMessages().unknownWarp
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        switch (args[1]) {
            case "icon":
                if (args.length != 3) {
                    player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
                    return false;
                }

                Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(args[2]);
                if (!xMaterial.isPresent()) {
                    player.sendMessage(StringUtils.color(teams.getMessages().noSuchMaterial
                            .replace("%prefix%", teams.getConfiguration().prefix)
                    ));
                    return false;
                }
                teamWarp.get().setIcon(xMaterial.get());
                player.sendMessage(StringUtils.color(teams.getMessages().warpIconSet
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                return true;
            case "description":
                if (args.length < 3) {
                    player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
                    return false;
                }

                String description = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                teamWarp.get().setDescription(description);
                player.sendMessage(StringUtils.color(teams.getMessages().warpDescriptionSet
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                return true;
            default:
                player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        List<TeamWarp> teamWarps = teams.getTeamManager().getTeamWarps(team);
        switch (args.length) {
            case 1:
                return teamWarps.stream().map(TeamWarp::getName).collect(Collectors.toList());
            case 2:
                return Arrays.asList("icon", "description");
            case 3:
                if (args[1].equalsIgnoreCase("icon")) {
                    return Arrays.stream(XMaterial.values()).map(XMaterial::name).collect(Collectors.toList());
                }
            default:
                return Collections.emptyList();
        }
    }
}
