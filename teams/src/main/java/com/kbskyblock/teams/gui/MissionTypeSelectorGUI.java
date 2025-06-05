package com.kbskyblock.teams.gui;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.configs.inventories.MissionTypeSelectorInventoryConfig;
import com.kbskyblock.teams.configs.inventories.NoItemGUI;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.gui.BackGUI;
import com.kbskyblock.core.utils.ItemStackUtils;
import com.kbskyblock.core.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class MissionTypeSelectorGUI<T extends Team, U extends KBSkyblockUser<T>> extends BackGUI {

    private final KBSkyblockTeams<T, U> teams;

    public MissionTypeSelectorGUI(Player player, KBSkyblockTeams<T, U> teams) {
        super(teams.getInventories().missionTypeSelectorGUI.background, player, teams.getInventories().backButton);
        this.teams = teams;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = teams.getInventories().missionTypeSelectorGUI;
        Inventory inventory = Bukkit.createInventory(this, noItemGUI.size, StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        super.addContent(inventory);


        MissionTypeSelectorInventoryConfig missionTypeSelectorInventoryConfig = teams.getInventories().missionTypeSelectorGUI;
        if (missionTypeSelectorInventoryConfig.daily.enabled) {
            inventory.setItem(missionTypeSelectorInventoryConfig.daily.item.slot, ItemStackUtils.makeItem(missionTypeSelectorInventoryConfig.daily.item));
        }

        if (missionTypeSelectorInventoryConfig.weekly.enabled) {
            inventory.setItem(missionTypeSelectorInventoryConfig.weekly.item.slot, ItemStackUtils.makeItem(missionTypeSelectorInventoryConfig.weekly.item));
        }

        if (missionTypeSelectorInventoryConfig.infinite.enabled) {
            inventory.setItem(missionTypeSelectorInventoryConfig.infinite.item.slot, ItemStackUtils.makeItem(missionTypeSelectorInventoryConfig.infinite.item));
        }

        if (missionTypeSelectorInventoryConfig.once.enabled) {
            inventory.setItem(missionTypeSelectorInventoryConfig.once.item.slot, ItemStackUtils.makeItem(missionTypeSelectorInventoryConfig.once.item));
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event);
        MissionTypeSelectorInventoryConfig missionTypeSelectorInventoryConfig = teams.getInventories().missionTypeSelectorGUI;

        if (event.getSlot() == missionTypeSelectorInventoryConfig.daily.item.slot && missionTypeSelectorInventoryConfig.daily.enabled) {
            teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().missionsCommand, new String[]{"Daily"});
        }

        if (event.getSlot() == missionTypeSelectorInventoryConfig.weekly.item.slot && missionTypeSelectorInventoryConfig.weekly.enabled) {
            teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().missionsCommand, new String[]{"Weekly"});
        }

        if (event.getSlot() == missionTypeSelectorInventoryConfig.infinite.item.slot && missionTypeSelectorInventoryConfig.infinite.enabled) {
            teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().missionsCommand, new String[]{"Infinite"});
        }

        if (event.getSlot() == missionTypeSelectorInventoryConfig.once.item.slot && missionTypeSelectorInventoryConfig.once.enabled) {
            teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().missionsCommand, new String[]{"Once"});
        }
    }
}
