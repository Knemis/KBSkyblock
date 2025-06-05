package com.kbskyblock.teams.gui;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.configs.inventories.NoItemGUI;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamWarp;
import com.kbskyblock.core.gui.BackGUI;
import com.kbskyblock.core.utils.ItemStackUtils;
import com.kbskyblock.core.utils.Placeholder;
import com.kbskyblock.core.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WarpsGUI<T extends Team, U extends KBSkyblockUser<T>> extends BackGUI {

    private final T team;
    private final KBSkyblockTeams<T, U> teams;

    public WarpsGUI(T team, Player player, KBSkyblockTeams<T, U> teams) {
        super(teams.getInventories().warpsGUI.background, player, teams.getInventories().backButton);
        this.team = team;
        this.teams = teams;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = teams.getInventories().warpsGUI;
        Inventory inventory = Bukkit.createInventory(this, noItemGUI.size, StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        super.addContent(inventory);

        AtomicInteger atomicInteger = new AtomicInteger(1);
        List<TeamWarp> teamWarps = teams.getTeamManager().getTeamWarps(team);
        for (TeamWarp teamWarp : teamWarps) {
            int slot = teams.getConfiguration().teamWarpSlots.get(atomicInteger.getAndIncrement());
            ItemStack itemStack = ItemStackUtils.makeItem(teams.getInventories().warpsGUI.item, Arrays.asList(
                    new Placeholder("island_name", team.getName()),
                    new Placeholder("warp_name", teamWarp.getName()),
                    new Placeholder("warp_description", teamWarp.getDescription() != null ? teamWarp.getDescription() : ""),
                    new Placeholder("warp_creator", Bukkit.getServer().getOfflinePlayer(teamWarp.getUser()).getName()),
                    new Placeholder("warp_create_time", teamWarp.getCreateTime().format(DateTimeFormatter.ofPattern(teams.getConfiguration().dateTimeFormat)))
            ));
            Material material = teamWarp.getIcon().parseMaterial();
            if (material != null) itemStack.setType(material);
            inventory.setItem(slot, itemStack);
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event);

        List<TeamWarp> teamWarps = teams.getTeamManager().getTeamWarps(team);
        for (Map.Entry<Integer, Integer> entrySet : teams.getConfiguration().teamWarpSlots.entrySet()) {
            if (entrySet.getValue() != event.getSlot()) continue;
            if (teamWarps.size() < entrySet.getKey()) continue;
            TeamWarp teamWarp = teamWarps.get(entrySet.getKey() - 1);
            switch (event.getClick()) {
                case LEFT:
                    teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().warpCommand, new String[]{teamWarp.getName()});
                    return;
                case RIGHT:
                    teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().deleteWarpCommand, new String[]{teamWarp.getName()});
            }
        }
    }
}
