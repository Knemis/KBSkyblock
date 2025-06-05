package com.kbskyblock.teams.gui;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.configs.inventories.NoItemGUI;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.sorting.TeamSorting;
import com.kbskyblock.core.gui.BackGUI;
import com.kbskyblock.core.utils.ItemStackUtils;
import com.kbskyblock.core.utils.StringUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@Setter
public class TopGUI<T extends Team, U extends KBSkyblockUser<T>> extends BackGUI {

    private TeamSorting<T> sortingType;
    private int page = 1;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private final KBSkyblockTeams<T, U> teams;

    public TopGUI(TeamSorting<T> sortingType, Player player, KBSkyblockTeams<T, U> teams) {
        super(teams.getInventories().topGUI.background, player, teams.getInventories().backButton);
        this.sortingType = sortingType;
        this.teams = teams;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = teams.getInventories().topGUI;
        Inventory inventory = Bukkit.createInventory(this, noItemGUI.size, StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        super.addContent(inventory);

        List<T> teams = teams.getTeamManager().getTeams(sortingType, true);

        for (int rank : teams.getConfiguration().teamTopSlots.keySet()) {
            int slot = teams.getConfiguration().teamTopSlots.get(rank);
            int actualRank = rank + (teams.getConfiguration().teamTopSlots.size() * (page - 1));
            if (teams.size() >= actualRank) {
                T team = teams.get(actualRank - 1);
                inventory.setItem(slot, ItemStackUtils.makeItem(teams.getInventories().topGUI.item, teams.getTeamsPlaceholderBuilder().getPlaceholders(team)));
            } else {
                inventory.setItem(slot, ItemStackUtils.makeItem(teams.getInventories().topGUI.filler));
            }
        }

        for (TeamSorting<T> sortingType : teams.getSortingTypes()) {
            inventory.setItem(sortingType.getItem().slot, ItemStackUtils.makeItem(sortingType.getItem()));
        }

        inventory.setItem(inventory.getSize() - 3, ItemStackUtils.makeItem(teams.getInventories().nextPage));
        inventory.setItem(inventory.getSize() - 7, ItemStackUtils.makeItem(teams.getInventories().previousPage));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event);

        if (event.getSlot() == teams.getInventories().topGUI.size - 7 && page > 1) {
            page--;
            event.getWhoClicked().openInventory(getInventory());
            return;
        }

        if (event.getSlot() == teams.getInventories().topGUI.size - 3 && teams.getTeamManager().getTeams().size() >= 1 + (teams.getConfiguration().teamTopSlots.size() * page)) {
            page++;
            event.getWhoClicked().openInventory(getInventory());
        }

        teams.getSortingTypes().stream().filter(sorting -> sorting.item.slot == event.getSlot()).findFirst().ifPresent(sortingType -> {
            this.sortingType = sortingType;
            addContent(event.getInventory());
        });
    }
}
