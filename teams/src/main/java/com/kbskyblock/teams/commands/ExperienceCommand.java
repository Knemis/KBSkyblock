package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.gui.BankGUI;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public class ExperienceCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public String adminPermission;

    public ExperienceCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds, String adminPermission) {
        super(args, description, syntax, permission, cooldownInSeconds);
        this.adminPermission = adminPermission;
    }

    @Override
    public boolean execute(CommandSender sender, String[] arguments, KBSkyblockTeams<T, U> teams) {
        if (arguments.length == 3) {
            Optional<T> team = teams.getTeamManager().getTeamViaNameOrPlayer(arguments[1]);
            if (!team.isPresent()) {
                sender.sendMessage(StringUtils.color(teams.getMessages().teamDoesntExistByName
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                return false;
            }
            int amount;
            try {
                amount = Integer.parseInt(arguments[2]);
            } catch (NumberFormatException exception) {
                sender.sendMessage(StringUtils.color(teams.getMessages().notANumber
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                return false;
            }

            if (!sender.hasPermission(adminPermission)) {
                sender.sendMessage(StringUtils.color(teams.getMessages().noPermission
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                return false;
            }

            switch (arguments[0].toLowerCase()) {
                case "give":
                    sender.sendMessage(StringUtils.color(teams.getMessages().gaveExperience
                            .replace("%prefix%", teams.getConfiguration().prefix)
                            .replace("%player%", arguments[1])
                            .replace("%amount%", String.valueOf(amount))
                    ));

                    team.get().setExperience(team.get().getExperience() + amount);
                    return true;
                case "remove":
                    sender.sendMessage(StringUtils.color(teams.getMessages().removedExperience
                            .replace("%prefix%", teams.getConfiguration().prefix)
                            .replace("%player%", arguments[1])
                            .replace("%amount%", String.valueOf(Math.min(amount, team.get().getExperience())))
                    ));

                    team.get().setExperience(team.get().getExperience() - amount);
                    return true;
                case "set":
                    sender.sendMessage(StringUtils.color(teams.getMessages().setExperience
                            .replace("%prefix%", teams.getConfiguration().prefix)
                            .replace("%player%", arguments[1])
                            .replace("%amount%", String.valueOf(Math.max(amount, 0)))
                    ));

                    team.get().setExperience(amount);
                    return true;
                default:
                    sender.sendMessage(StringUtils.color(syntax
                            .replace("%prefix%", teams.getConfiguration().prefix)
                    ));
                    return false;
            }
        }
        if (arguments.length != 0) {
            sender.sendMessage(StringUtils.color(syntax
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }

        return teams.getCommandManager().executeCommand(sender, teams.getCommands().infoCommand, arguments);
    }

    @Override
    public boolean execute(U user, T team, String[] arguments, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        player.openInventory(new BankGUI<>(team, player, teams).getInventory());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args, KBSkyblockTeams<T, U> teams) {
        if (!commandSender.hasPermission(adminPermission)) return Collections.emptyList();
        switch (args.length) {
            case 1:
                return Arrays.asList("give", "set", "remove");
            case 2:
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            case 3:
                return Arrays.asList("1", "10", "100");
            default:
                return Collections.emptyList();
        }
    }
}
