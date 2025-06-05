package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.PermissionType;
import com.kbskyblock.teams.Setting;
import com.kbskyblock.teams.api.SettingUpdateEvent;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamSetting;
import com.kbskyblock.teams.gui.SettingsGUI;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public class SettingsCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public SettingsCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (args.length == 0) {
            player.openInventory(new SettingsGUI<>(team, player, teams).getInventory());
            return true;
        } else if (args.length == 2) {
            if (!teams.getTeamManager().getTeamPermission(team, user, PermissionType.SETTINGS)) {
                player.sendMessage(StringUtils.color(teams.getMessages().cannotChangeSettings
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                return false;
            }
            String settingKey = args[0];
            for (Map.Entry<String, Setting> setting : teams.getSettingsList().entrySet()) {
                if (!setting.getValue().getDisplayName().equalsIgnoreCase(settingKey)) continue;
                TeamSetting teamSetting = teams.getTeamManager().getTeamSetting(team, setting.getKey());
                Optional<String> value = setting.getValue().getValues().stream().filter(s -> s.equalsIgnoreCase(args[1])).findFirst();

                if (!value.isPresent() || teamSetting == null) {
                    player.sendMessage(StringUtils.color(teams.getMessages().invalidSettingValue
                            .replace("%prefix%", teams.getConfiguration().prefix)
                    ));
                    return false;
                }

                teamSetting.setValue(value.get());
                player.sendMessage(StringUtils.color(teams.getMessages().settingSet
                        .replace("%prefix%", teams.getConfiguration().prefix)
                        .replace("%setting%", setting.getValue().getDisplayName())
                        .replace("%value%", value.get())
                ));

                Bukkit.getPluginManager().callEvent(new SettingUpdateEvent<>(team, user, setting.getKey(), value.get()));
                return true;
            }
            player.sendMessage(StringUtils.color(teams.getMessages().invalidSetting
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args, KBSkyblockTeams<T, U> teams) {
        switch (args.length) {
            case 1:
                return teams.getSettingsList().values().stream().map(Setting::getDisplayName).collect(Collectors.toList());
            case 2:
                for (Map.Entry<String, Setting> setting : teams.getSettingsList().entrySet()) {
                    if (!setting.getValue().getDisplayName().equalsIgnoreCase(args[0])) continue;
                    return setting.getValue().getValues();
                }
            default:
                return Collections.emptyList();
        }
    }

}
