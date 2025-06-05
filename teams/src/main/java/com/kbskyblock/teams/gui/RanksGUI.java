package com.kbskyblock.teams.gui;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.UserRank;
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

import java.util.Map;

public class RanksGUI<T extends Team, U extends KBSkyblockUser<T>> extends BackGUI {

    private final KBSkyblockTeams<T, U> teams;
    private final T team;

    public RanksGUI(T team, Player player, KBSkyblockTeams<T, U> teams) {
        super(teams.getInventories().ranksGUI.background, player, teams.getInventories().backButton);
        this.team = team;
        this.teams = teams;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, teams.getInventories().ranksGUI.size, StringUtils.color(teams.getInventories().ranksGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        super.addContent(inventory);

        for (UserRank userRank : teams.getUserRanks().values()) {
            inventory.setItem(userRank.item.slot, ItemStackUtils.makeItem(userRank.item));
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event);

        for (Map.Entry<Integer, UserRank> userRank : teams.getUserRanks().entrySet()) {
            if (event.getSlot() != userRank.getValue().item.slot) continue;
            event.getWhoClicked().openInventory(new PermissionsGUI<>(team, userRank.getKey(), (Player) event.getWhoClicked(), teams).getInventory());
            return;
        }
    }
}
