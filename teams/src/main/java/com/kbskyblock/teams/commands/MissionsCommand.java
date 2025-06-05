package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.configs.inventories.MissionTypeSelectorInventoryConfig;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.gui.MissionGUI;
import com.kbskyblock.teams.gui.MissionTypeSelectorGUI;
import com.kbskyblock.teams.missions.MissionType;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class MissionsCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public MissionsCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        MissionTypeSelectorInventoryConfig missionTypeSelectorInventoryConfig = teams.getInventories().missionTypeSelectorGUI;
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "daily":
                    if (missionTypeSelectorInventoryConfig.daily.enabled) {
                        player.openInventory(new MissionGUI<>(team, MissionType.DAILY, player, teams).getInventory());
                    } else {
                        player.openInventory(new MissionTypeSelectorGUI<>(player, teams).getInventory());
                    }
                    return true;
                case "weekly":
                    if (missionTypeSelectorInventoryConfig.weekly.enabled) {
                        player.openInventory(new MissionGUI<>(team, MissionType.WEEKLY, player, teams).getInventory());
                    } else {
                        player.openInventory(new MissionTypeSelectorGUI<>(player, teams).getInventory());
                    }
                    return true;
                case "infinite":
                    if (missionTypeSelectorInventoryConfig.infinite.enabled) {
                        player.openInventory(new MissionGUI<>(team, MissionType.INFINITE, player, teams).getInventory());
                    } else {
                        player.openInventory(new MissionTypeSelectorGUI<>(player, teams).getInventory());
                    }
                    return true;
                case "once":
                    if (missionTypeSelectorInventoryConfig.once.enabled) {
                        player.openInventory(new MissionGUI<>(team, MissionType.ONCE, player, teams).getInventory());
                    } else {
                        player.openInventory(new MissionTypeSelectorGUI<>(player, teams).getInventory());
                    }
                    return true;
            }
        }
        player.openInventory(new MissionTypeSelectorGUI<>(player, teams).getInventory());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args, KBSkyblockTeams<T, U> teams) {
        MissionTypeSelectorInventoryConfig missionTypeSelectorInventoryConfig = teams.getInventories().missionTypeSelectorGUI;
        List<String> missionTypes = new ArrayList<>();
        if (missionTypeSelectorInventoryConfig.daily.enabled) {
            missionTypes.add("Daily");
        }

        if (missionTypeSelectorInventoryConfig.weekly.enabled) {
            missionTypes.add("Weekly");
        }

        if (missionTypeSelectorInventoryConfig.infinite.enabled) {
            missionTypes.add("Infinite");
        }

        if (missionTypeSelectorInventoryConfig.once.enabled) {
            missionTypes.add("Once");
        }
        return missionTypes;
    }
}
