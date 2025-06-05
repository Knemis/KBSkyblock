package com.kbskyblock.teams.gui;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.Setting;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamSetting;
import com.kbskyblock.core.gui.BackGUI;
import com.kbskyblock.core.utils.ItemStackUtils;
import com.kbskyblock.core.utils.Placeholder;
import com.kbskyblock.core.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public class SettingsGUI<T extends Team, U extends KBSkyblockUser<T>> extends BackGUI {

    private final KBSkyblockTeams<T, U> teams;
    private final T team;

    public SettingsGUI(T team, Player player, KBSkyblockTeams<T, U> teams) {
        super(teams.getInventories().settingsGUI.background, player, teams.getInventories().backButton);
        this.teams = teams;
        this.team = team;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, teams.getInventories().settingsGUI.size, StringUtils.color(teams.getInventories().settingsGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        super.addContent(inventory);

        for (Map.Entry<String, Setting> setting : teams.getSettingsList().entrySet()) {
            TeamSetting teamSetting = teams.getTeamManager().getTeamSetting(team, setting.getKey());
            if (teamSetting == null) continue;

            String teamSettingDisplay = teamSetting.getValue();
            switch(teamSetting.getValue()) {
                case "Enabled": {
                    teamSettingDisplay = teams.getMessages().enabledPlaceholder;
                    break;
                }
                case "Disabled": {
                    teamSettingDisplay = teams.getMessages().disabledPlaceholder;
                    break;
                }
                case "Private": {
                    teamSettingDisplay = teams.getMessages().privatePlaceholder;
                    break;
                }
                case "Public": {
                    teamSettingDisplay = teams.getMessages().publicPlaceholder;
                    break;
                }
                case "Server": {
                    teamSettingDisplay = teams.getMessages().serverPlaceholder;
                    break;
                }
                case "Sunny": {
                    teamSettingDisplay = teams.getMessages().sunnyPlaceholder;
                    break;
                }
                case "Raining": {
                    teamSettingDisplay = teams.getMessages().rainingPlaceholder;
                    break;
                }
                case "Sunrise": {
                    teamSettingDisplay = teams.getMessages().sunrisePlaceholder;
                    break;
                }
                case "Day": {
                    teamSettingDisplay = teams.getMessages().dayPlaceholder;
                    break;
                }
                case "Morning": {
                    teamSettingDisplay = teams.getMessages().morningPlaceholder;
                    break;
                }
                case "Noon": {
                    teamSettingDisplay = teams.getMessages().noonPlaceholder;
                    break;
                }
                case "Sunset": {
                    teamSettingDisplay = teams.getMessages().sunsetPlaceholder;
                    break;
                }
                case "Night": {
                    teamSettingDisplay = teams.getMessages().nightPlaceholder;
                    break;
                }
                case "Midnight": {
                    teamSettingDisplay = teams.getMessages().midnightPlaceholder;
                    break;
                }
            }

            inventory.setItem(setting.getValue().getItem().slot, ItemStackUtils.makeItem(setting.getValue().getItem(), Collections.singletonList(
                    new Placeholder("value", teamSettingDisplay)
            )));
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event);

        for (Map.Entry<String, Setting> setting : teams.getSettingsList().entrySet()) {
            if (setting.getValue().getItem().slot != event.getSlot()) continue;

            TeamSetting teamSetting = teams.getTeamManager().getTeamSetting(team, setting.getKey());
            if (teamSetting == null) continue;
            int currentIndex = setting.getValue().getValues().indexOf(teamSetting.getValue());
            String newValue = setting.getValue().getValues().get(setting.getValue().getValues().size() > currentIndex + 1 ? currentIndex + 1 : 0);
            teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().settingsCommand, new String[]{setting.getValue().getDisplayName(), newValue});
            return;
        }
    }
}
