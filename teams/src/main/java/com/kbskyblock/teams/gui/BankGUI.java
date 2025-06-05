package com.kbskyblock.teams.gui;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.bank.BankItem;
import com.kbskyblock.teams.configs.inventories.NoItemGUI;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamBank;
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
import java.util.Optional;

public class BankGUI<T extends Team, U extends KBSkyblockUser<T>> extends BackGUI {

    private final T team;
    private final KBSkyblockTeams<T, U> teams;

    public BankGUI(T team, Player player, KBSkyblockTeams<T, U> teams) {
        super(teams.getInventories().bankGUI.background, player, teams.getInventories().backButton);
        this.team = team;
        this.teams = teams;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = teams.getInventories().bankGUI;
        Inventory inventory = Bukkit.createInventory(this, noItemGUI.size, StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        super.addContent(inventory);

        for (BankItem bankItem : teams.getBankItemList()) {
            TeamBank teamBank = teams.getTeamManager().getTeamBank(team, bankItem.getName());
            inventory.setItem(bankItem.getItem().slot, ItemStackUtils.makeItem(bankItem.getItem(), Collections.singletonList(
                    new Placeholder("amount", teams.getConfiguration().numberFormatter.format(teamBank.getNumber()))
            )));
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event);

        Optional<BankItem> bankItem = teams.getBankItemList().stream().filter(item -> item.getItem().slot == event.getSlot()).findFirst();
        if (!bankItem.isPresent()) return;

        switch (event.getClick()) {
            case LEFT:
                teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().withdrawCommand, new String[]{bankItem.get().getName(), String.valueOf(bankItem.get().getDefaultAmount())});
                break;
            case RIGHT:
                teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().depositCommand, new String[]{bankItem.get().getName(), String.valueOf(bankItem.get().getDefaultAmount())});
                break;
            case SHIFT_LEFT:
                teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().withdrawCommand, new String[]{bankItem.get().getName(), String.valueOf(Double.MAX_VALUE)});
                break;
            case SHIFT_RIGHT:
                teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().depositCommand, new String[]{bankItem.get().getName(), String.valueOf(Double.MAX_VALUE)});
                break;
        }

        addContent(event.getInventory());
    }
}
