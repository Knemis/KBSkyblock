package com.kbskyblock.teams.gui;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.configs.inventories.NoItemGUI;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.gui.PagedGUI;
import com.kbskyblock.core.utils.ItemStackUtils;
import com.kbskyblock.core.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class MembersGUI<T extends Team, U extends KBSkyblockUser<T>> extends PagedGUI<U> {

    private final KBSkyblockTeams<T, U> teams;
    private final T team;

    public MembersGUI(T team, Player player, KBSkyblockTeams<T, U> teams) {
        super(
                1,
                teams.getInventories().membersGUI.size,
                teams.getInventories().membersGUI.background,
                teams.getInventories().previousPage,
                teams.getInventories().nextPage,
                player,
                teams.getInventories().backButton
        );
        this.teams = teams;
        this.team = team;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = teams.getInventories().membersGUI;
        Inventory inventory = Bukkit.createInventory(this, getSize(), StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public Collection<U> getPageObjects() {
        return teams.getTeamManager().getTeamMembers(team);
    }

    @Override
    public ItemStack getItemStack(U user) {
        return ItemStackUtils.makeItem(teams.getInventories().membersGUI.item, teams.getUserPlaceholderBuilder().getPlaceholders(user));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event);

        U user = getItem(event.getSlot());
        if (user == null) return;

        switch (event.getClick()) {
            case LEFT:
                if (user.getUserRank() != 1) {
                    teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().demoteCommand, new String[]{user.getName()});
                } else {
                    teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().kickCommand, new String[]{user.getName()});
                }
                break;
            case RIGHT:
                teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().promoteCommand, new String[]{user.getName()});
                break;
        }
    }
}
