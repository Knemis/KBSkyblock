package com.kbskyblock.teams.gui;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.Permission;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.gui.BackGUI;
import com.kbskyblock.core.utils.ItemStackUtils;
import com.kbskyblock.core.utils.Placeholder;
import com.kbskyblock.core.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public class PermissionsGUI<T extends Team, U extends KBSkyblockUser<T>> extends BackGUI {

    private final KBSkyblockTeams<T, U> teams;
    private final T team;
    @Getter
    private final int rank;
    @Getter
    private int page;

    public PermissionsGUI(T team, int rank, Player player, KBSkyblockTeams<T, U> teams) {
        super(teams.getInventories().permissionsGUI.background, player, teams.getInventories().backButton);
        this.teams = teams;
        this.team = team;
        this.rank = rank;
        this.page = 1;
    }

    public PermissionsGUI(T team, int rank, int page, Player player, KBSkyblockTeams<T, U> teams) {
        super(teams.getInventories().permissionsGUI.background, player, teams.getInventories().backButton);

        this.teams = teams;
        this.team = team;
        this.rank = rank;
        this.page = page;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, teams.getInventories().permissionsGUI.size, StringUtils.color(teams.getInventories().permissionsGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        super.addContent(inventory);

        for (Map.Entry<String, Permission> permission : teams.getPermissionList().entrySet()) {
            if (permission.getValue().getPage() != page) continue;
            boolean allowed = teams.getTeamManager().getTeamPermission(team, rank, permission.getKey());
            inventory.setItem(permission.getValue().getItem().slot, ItemStackUtils.makeItem(permission.getValue().getItem(), Collections.singletonList(new Placeholder("permission", allowed ? teams.getPermissions().allowed : teams.getPermissions().denied))));
        }

        inventory.setItem(inventory.getSize() - 3, ItemStackUtils.makeItem(teams.getInventories().nextPage));
        inventory.setItem(inventory.getSize() - 7, ItemStackUtils.makeItem(teams.getInventories().previousPage));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event);

        if (event.getSlot() == teams.getInventories().permissionsGUI.size - 7 && page > 1) {
            page--;
            event.getWhoClicked().openInventory(getInventory());
            return;
        }

        if (event.getSlot() == teams.getInventories().permissionsGUI.size - 3 && teams.getPermissionList().values().stream().anyMatch(permission -> permission.getPage() == page + 1)) {
            page++;
            event.getWhoClicked().openInventory(getInventory());
        }

        for (Map.Entry<String, Permission> permission : teams.getPermissionList().entrySet()) {
            if (permission.getValue().getItem().slot != event.getSlot()) continue;
            if (permission.getValue().getPage() != page) continue;

            teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().setPermissionCommand, new String[]{permission.getKey(), teams.getUserRanks().get(rank).name});
            return;
        }
    }
}
