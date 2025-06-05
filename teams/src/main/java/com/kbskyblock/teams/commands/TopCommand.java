package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.gui.TopGUI;
import com.kbskyblock.teams.sorting.TeamSorting;
import com.kbskyblock.core.utils.Placeholder;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class TopCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {

    public String adminPermission;

    public TopCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds, String adminPermission) {
        super(args, description, syntax, permission, cooldownInSeconds);
        this.adminPermission = adminPermission;
    }

    @Override
    public boolean execute(CommandSender sender, String[] arguments, KBSkyblockTeams<T, U> teams) {
        int listLength = 10;
        TeamSorting<T> sortingType = teams.getSortingTypes().get(0);
        boolean excludePrivate = !sender.hasPermission(adminPermission);

        if (sender instanceof Player && arguments.length == 0) return sendGUI((Player) sender, teams);

        switch (arguments.length) {
            case 3: {
                try {
                    listLength = Math.min(Integer.parseInt(arguments[2]), 100);
                } catch (NumberFormatException ignored) {}
            }
            case 2: {
                for(TeamSorting<T> pluginSortingType : teams.getSortingTypes()) {
                    if (arguments[1].equalsIgnoreCase(pluginSortingType.getName())) sortingType = pluginSortingType;
                }
            }
            case 1: {
                if (!arguments[0].equalsIgnoreCase("list")) {
                    sender.sendMessage(StringUtils.color(syntax.replace("prefix", teams.getConfiguration().prefix)));
                    return false;
                }
            }
            default: {
                sendList(sender, teams, sortingType, listLength, excludePrivate);
                return true;
            }
        }
    }

     public boolean sendGUI(Player player, KBSkyblockTeams<T, U> teams) {
         player.openInventory(new TopGUI<>(teams.getTop().valueTeamSort, player, teams).getInventory());
         return true;
    }

    public void sendList(CommandSender sender, KBSkyblockTeams<T, U> teams, TeamSorting<T> sortingType, int listLength, boolean excludePrivate) {
        List<T> teamList = teams.getTeamManager().getTeams(sortingType, excludePrivate);

        sender.sendMessage(StringUtils.color(StringUtils.getCenteredMessage(teams.getMessages().topCommandHeader.replace("%sort_type%", sortingType.getName()), teams.getMessages().topCommandFiller)));

        for (int i = 0; i < listLength;  i++) {
            if(i == sortingType.getSortedTeams(teams).size()) break;
            T team = teamList.get(i);
            List<Placeholder> placeholders = teams.getTeamsPlaceholderBuilder().getPlaceholders(team);
            placeholders.add(new Placeholder("value", teams.getConfiguration().numberFormatter.format(sortingType.getValue(team))));
            placeholders.add(new Placeholder("rank", String.valueOf(i+1)));

            String color = "&7";
            switch(i) {
                case 0: {
                    color = teams.getMessages().topFirstColor;
                    break;
                }
                case 1: {
                    color = teams.getMessages().topSecondColor;
                    break;
                }
                case 2: {
                    color = teams.getMessages().topThirdColor;
                    break;
                }
            }
            placeholders.add(new Placeholder("color", color));


            sender.sendMessage(StringUtils.color(StringUtils.processMultiplePlaceholders(teams.getMessages().topCommandMessage, placeholders)));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args, KBSkyblockTeams<T, U> teams) {
        if (!commandSender.hasPermission(adminPermission)) return Collections.singletonList("");
        switch(args.length) {
            case 1: {
                return Collections.singletonList("list");
            }
            case 2: {
                return teams.getSortingTypes().stream().map(TeamSorting::getName).collect(Collectors.toList());
            }
            case 3: {
                return Collections.singletonList("10");
            }
            default: {
                return null;
            }
        }
    }
}
