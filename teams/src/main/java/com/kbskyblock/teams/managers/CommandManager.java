package com.kbskyblock.teams.manager;

import com.kbskyblock.core.utils.StringUtils;
import com.kbskyblock.core.utils.TimeUtils;
import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.commands.Command;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public abstract class CommandManager<T extends Team, U extends KBSkyblockUser<T>> implements CommandExecutor, TabCompleter {

    @Getter
    private final List<Command<T, U>> commands = new ArrayList<>();
    @Getter
    private final String command;
    @Getter
    private final String color;
    private final KBSkyblockTeams<T, U> teams;

    public CommandManager(KBSkyblockTeams<T, U> teams, String color, String command) {
        this.teams = teams;
        this.command = command;
        this.color = color;
        teams.getCommand(command).setExecutor(this);
        teams.getCommand(command).setTabCompleter(this);
        registerCommands();
    }

    public void registerCommands() {
        registerCommand(teams.getCommands().aboutCommand);
        registerCommand(teams.getCommands().createCommand);
        registerCommand(teams.getCommands().membersCommand);
        registerCommand(teams.getCommands().permissionsCommand);
        registerCommand(teams.getCommands().setPermissionCommand);
        registerCommand(teams.getCommands().promoteCommand);
        registerCommand(teams.getCommands().demoteCommand);
        registerCommand(teams.getCommands().helpCommand);
        registerCommand(teams.getCommands().reloadCommand);
        registerCommand(teams.getCommands().inviteCommand);
        registerCommand(teams.getCommands().unInviteCommand);
        registerCommand(teams.getCommands().invitesCommand);
        registerCommand(teams.getCommands().trustCommand);
        registerCommand(teams.getCommands().unTrustCommand);
        registerCommand(teams.getCommands().trustsCommand);
        registerCommand(teams.getCommands().kickCommand);
        registerCommand(teams.getCommands().leaveCommand);
        registerCommand(teams.getCommands().deleteCommand);
        registerCommand(teams.getCommands().infoCommand);
        registerCommand(teams.getCommands().descriptionCommand);
        registerCommand(teams.getCommands().renameCommand);
        registerCommand(teams.getCommands().setHomeCommand);
        registerCommand(teams.getCommands().homeCommand);
        registerCommand(teams.getCommands().bypassCommand);
        registerCommand(teams.getCommands().transferCommand);
        registerCommand(teams.getCommands().joinCommand);
        registerCommand(teams.getCommands().bankCommand);
        registerCommand(teams.getCommands().depositCommand);
        registerCommand(teams.getCommands().withdrawCommand);
        registerCommand(teams.getCommands().chatCommand);
        registerCommand(teams.getCommands().boostersCommand);
        registerCommand(teams.getCommands().upgradesCommand);
        registerCommand(teams.getCommands().flyCommand);
        registerCommand(teams.getCommands().topCommand);
        registerCommand(teams.getCommands().recalculateCommand);
        registerCommand(teams.getCommands().warpsCommand);
        registerCommand(teams.getCommands().warpCommand);
        registerCommand(teams.getCommands().setWarpCommand);
        registerCommand(teams.getCommands().deleteWarpCommand);
        registerCommand(teams.getCommands().editWarpCommand);
        registerCommand(teams.getCommands().missionsCommand);
        registerCommand(teams.getCommands().rewardsCommand);
        registerCommand(teams.getCommands().experienceCommand);
        registerCommand(teams.getCommands().shopCommand);
        registerCommand(teams.getCommands().settingsCommand);
        registerCommand(teams.getCommands().blockValueCommand);
        registerCommand(teams.getCommands().levelCommand);
    }

    public void registerCommand(Command<T, U> command) {
        if (!command.enabled) return;
        int index = Collections.binarySearch(commands, command, Comparator.comparing(cmd -> cmd.aliases.get(0)));
        commands.add(index < 0 ? -(index + 1) : index, command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            noArgsDefault(commandSender);
            return true;
        }

        for (Command<T, U> command : commands) {
            // We don't want to execute other commands or ones that are disabled
            if (!command.aliases.contains(args[0])) continue;

            return executeCommand(commandSender, command, Arrays.copyOfRange(args, 1, args.length));
        }

        // Unknown command message
        commandSender.sendMessage(StringUtils.color(teams.getMessages().unknownCommand
                .replace("%prefix%", teams.getConfiguration().prefix)
        ));
        return false;
    }

    public boolean executeCommand(CommandSender commandSender, Command<T, U> command, String[] args) {
        if (!command.hasPermission(commandSender, teams)) {
            commandSender.sendMessage(StringUtils.color(teams.getMessages().noPermission
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }

        if (command.isOnCooldown(commandSender, teams)) {
            Duration remainingTime = command.getCooldownProvider().getRemainingTime(commandSender);
            String formattedTime = TimeUtils.formatDuration(teams.getMessages().activeCooldown, remainingTime);

            commandSender.sendMessage(StringUtils.color(formattedTime
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }

        if (command.execute(commandSender, args, teams)) {
            command.getCooldownProvider().applyCooldown(commandSender);
        }
        return true;
    }

    public abstract void noArgsDefault(@NotNull CommandSender commandSender);

    private List<String> getTabComplete(CommandSender commandSender, String[] args) {
        if (args.length == 1) {
            ArrayList<String> result = new ArrayList<>();
            for (Command<T, U> command : commands) {
                if(command.isSuperSecretCommand()) continue;
                for (String alias : command.aliases) {
                    if (!alias.toLowerCase().startsWith(args[0].toLowerCase())) continue;
                    if (command.hasPermission(commandSender, teams)) {
                        result.add(alias);
                    }
                }
            }
            return result.stream().sorted().collect(Collectors.toList());
        }

        for (Command<T, U> command : commands) {
            if(command.isSuperSecretCommand()) continue;
            if (!command.aliases.contains(args[0].toLowerCase())) continue;
            if (command.hasPermission(commandSender, teams)) {
                return command.onTabComplete(commandSender, Arrays.copyOfRange(args, 1, args.length), teams);
            }
        }

        // We currently don't want to tab-completion here
        // Return a new List so it isn't a list of online players
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, org.bukkit.command.@NotNull Command cmd, @NotNull String label, String[] args) {
        List<String> tabComplete = getTabComplete(commandSender, args);
        if (tabComplete == null) return null;
        return tabComplete.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .sorted()
                .collect(Collectors.toList());
    }
}
