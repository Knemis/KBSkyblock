package com.kbskyblock.teams.gui;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.gui.GUI;
import com.kbskyblock.core.utils.InventoryUtils;
import com.kbskyblock.core.utils.ItemStackUtils;
import com.kbskyblock.core.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class ConfirmationGUI<T extends Team, U extends KBSkyblockUser<T>> implements GUI {

    private final @NotNull Runnable runnable;
    private final KBSkyblockTeams<T, U> teams;

    public ConfirmationGUI(@NotNull Runnable runnable, KBSkyblockTeams<T, U> teams) {
        this.runnable = runnable;
        this.teams = teams;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, teams.getInventories().confirmationGUI.size, StringUtils.color(teams.getInventories().confirmationGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        inventory.clear();
        InventoryUtils.fillInventory(inventory, teams.getInventories().confirmationGUI.background);

        inventory.setItem(teams.getInventories().confirmationGUI.no.slot, ItemStackUtils.makeItem(teams.getInventories().confirmationGUI.no));
        inventory.setItem(teams.getInventories().confirmationGUI.yes.slot, ItemStackUtils.makeItem(teams.getInventories().confirmationGUI.yes));
    }

    /**
     * Called when there is a click in this GUI.
     * Cancelled automatically.
     *
     * @param event The InventoryClickEvent provided by Bukkit
     */
    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getSlot() == teams.getInventories().confirmationGUI.no.slot) {
            player.closeInventory();
        } else if (event.getSlot() == teams.getInventories().confirmationGUI.yes.slot) {
            runnable.run();
            player.closeInventory();
        }
    }
}
