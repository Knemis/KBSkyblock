package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.SettingType;
import com.kbskyblock.teams.configs.inventories.BlockValuesTypeSelectorInventoryConfig;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamSetting;
import com.kbskyblock.teams.gui.BlockValueGUI;
import com.kbskyblock.teams.gui.BlockValuesTypeSelectorGUI;
import com.kbskyblock.teams.gui.SpawnerValueGUI;
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
public class BlockValueCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {

    public BlockValueCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, String[] args, KBSkyblockTeams<T, U> teams) {

        Player player = user.getPlayer();
        Optional<T> team;

        BlockValuesTypeSelectorInventoryConfig blockValuesTypeSelectorInventoryConfig = teams.getInventories().blockValuesTypeSelectorGUI;

        String teamArg = args.length > 1 ? args[0] : player.getName();
        team = teams.getTeamManager().getTeamViaNameOrPlayer(teamArg);

        if (!team.isPresent() && args.length >= 1) {
            player.sendMessage(StringUtils.color(teams.getMessages().teamDoesntExistByName.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }

        if (!team.isPresent()) {
            player.sendMessage(StringUtils.color(teams.getMessages().dontHaveTeam.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }

        TeamSetting teamSetting = teams.getTeamManager().getTeamSetting(team.get(), SettingType.VALUE_VISIBILITY.getSettingKey());

        if (teamSetting != null && teamSetting.getValue().equalsIgnoreCase("Private") && !teams.getTeamManager().getTeamMembers(team.get()).contains(user) && !user.isBypassing()) {
            player.sendMessage(StringUtils.color(teams.getMessages().teamIsPrivate.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }

        if (args.length == 0) {
            player.openInventory(new BlockValuesTypeSelectorGUI<>(teamArg, player, teams).getInventory());
            return true;
        }

        switch (args[args.length - 1]) {
            case ("blocks"): {
                if (blockValuesTypeSelectorInventoryConfig.blocks.enabled) {
                    player.openInventory(new BlockValueGUI<>(team.get(), player, teams).getInventory());
                    return true;
                }
            }
            case ("spawners"): {
                if (blockValuesTypeSelectorInventoryConfig.spawners.enabled) {
                    player.openInventory(new SpawnerValueGUI<>(team.get(), player, teams).getInventory());
                    return true;
                }
            }
            default: {
                player.openInventory(new BlockValuesTypeSelectorGUI<>(teamArg, player, teams).getInventory());
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args, KBSkyblockTeams<T, U> teams) {

        switch (args.length) {
            case 1:
                return Arrays.asList("blocks", "spawners");
            case 2:
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }
}