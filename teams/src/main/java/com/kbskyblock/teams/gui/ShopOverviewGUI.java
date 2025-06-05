package com.kbskyblock.teams.gui;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.configs.Shop;
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

import java.util.Map;

public class ShopOverviewGUI<T extends Team, U extends KBSkyblockUser<T>> extends BackGUI {
    private final KBSkyblockTeams<T, U> teams;

    public ShopOverviewGUI(Player player, KBSkyblockTeams<T, U> teams) {
        super(teams.getInventories().shopOverviewGUI.background, player, teams.getInventories().backButton);
        this.teams = teams;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = teams.getInventories().shopOverviewGUI;
        Inventory inventory = Bukkit.createInventory(this, noItemGUI.size, StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        super.addContent(inventory);

        for (Shop.ShopCategory category : teams.getShop().categories.values()) {
            inventory.setItem(category.item.slot, ItemStackUtils.makeItem(category.item));
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        for (Map.Entry<String, Shop.ShopCategory> category : teams.getShop().categories.entrySet()) {
            if (event.getSlot() != category.getValue().item.slot) continue;
            event.getWhoClicked().openInventory(new ShopCategoryGUI<>(category.getKey(), (Player) event.getWhoClicked(), 1, teams).getInventory());
            return;
        }
        super.onInventoryClick(event);
    }
}


