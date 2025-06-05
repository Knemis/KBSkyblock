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
public class RenameCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public String adminPermission;

    public RenameCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds, String adminPermission) {
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
            String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            if(changeName(team.get(), name, player, teams)){
                player.sendMessage(StringUtils.color(teams.getMessages().changedPlayerName
                        .replace("%prefix%", teams.getConfiguration().prefix)
                        .replace("%name%", team.get().getName())
                        .replace("%player%", args[0])
                ));
                return true;
            }
            return false;
        }
        return super.execute(user, args, teams);
    }

    @Override
    public boolean execute(U user, T team, String[] arguments, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (!teams.getTeamManager().getTeamPermission(team, user, PermissionType.RENAME)) {
            player.sendMessage(StringUtils.color(teams.getMessages().cannotChangeName
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        return changeName(team, String.join(" ", arguments), player, teams);
    }

    private boolean changeName(T team, String name, Player player, KBSkyblockTeams<T, U> teams) {
        Optional<T> teamViaName = teams.getTeamManager().getTeamViaName(name);
        if (teamViaName.isPresent() && teamViaName.get().getId() != team.getId()) {
            player.sendMessage(StringUtils.color(teams.getMessages().teamNameAlreadyExists
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        if (name.length() < teams.getConfiguration().minTeamNameLength) {
            player.sendMessage(StringUtils.color(teams.getMessages().teamNameTooShort
                    .replace("%prefix%", teams.getConfiguration().prefix)
                    .replace("%min_length%", String.valueOf(teams.getConfiguration().minTeamNameLength))
            ));
            return false;
        }
        if (name.length() > teams.getConfiguration().maxTeamNameLength) {
            player.sendMessage(StringUtils.color(teams.getMessages().teamNameTooLong
                    .replace("%prefix%", teams.getConfiguration().prefix)
                    .replace("%max_length%", String.valueOf(teams.getConfiguration().maxTeamNameLength))
            ));
            return false;
        }
        team.setName(name);
        teams.getTeamManager().getTeamMembers(team).stream().map(U::getPlayer).filter(Objects::nonNull).forEach(member ->
                member.sendMessage(StringUtils.color(teams.getMessages().nameChanged
                        .replace("%prefix%", teams.getConfiguration().prefix)
                        .replace("%player%", player.getName())
                        .replace("%name%", name)
                ))
        );
        return true;
    }

}
