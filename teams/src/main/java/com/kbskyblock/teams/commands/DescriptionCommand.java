package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.PermissionType;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor
public class DescriptionCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public String adminPermission;

    public DescriptionCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds, String adminPermission) {
        super(args, description, syntax, permission, cooldownInSeconds);
        this.adminPermission = adminPermission;
    }

    @Override
    public boolean execute(U user, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (args.length == 0) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }
        Optional<T> team = teams.getTeamManager().getTeamViaNameOrPlayer(args[0]);
        if (team.isPresent() && player.hasPermission(adminPermission)) {
            String description = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            changeDescription(team.get(), description, player, teams);
            player.sendMessage(StringUtils.color(teams.getMessages().changedPlayerDescription
                    .replace("%prefix%", teams.getConfiguration().prefix)
                    .replace("%name%", team.get().getName())
                    .replace("%description%", description)
            ));
            return true;
        }
        return super.execute(user, args, teams);
    }

    @Override
    public boolean execute(U user, T team, String[] arguments, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (!teams.getTeamManager().getTeamPermission(team, user, PermissionType.DESCRIPTION)) {
            player.sendMessage(StringUtils.color(teams.getMessages().cannotChangeDescription
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        changeDescription(team, String.join(" ", arguments), player, teams);
        return true;
    }

    private void changeDescription(T team, String description, Player player, KBSkyblockTeams<T, U> teams) {
        team.setDescription(description);
        teams.getTeamManager().getTeamMembers(team).stream().map(U::getPlayer).filter(Objects::nonNull).forEach(member ->
                member.sendMessage(StringUtils.color(teams.getMessages().descriptionChanged
                        .replace("%prefix%", teams.getConfiguration().prefix)
                        .replace("%player%", player.getName())
                        .replace("%description%", description)
                ))
        );
    }
}
