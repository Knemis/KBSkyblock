package com.kbskyblock.teams.gui;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.configs.inventories.NoItemGUI;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamEnhancement;
import com.kbskyblock.teams.enhancements.Enhancement;
import com.kbskyblock.teams.enhancements.EnhancementData;
import com.kbskyblock.teams.enhancements.EnhancementType;
import com.kbskyblock.core.gui.BackGUI;
import com.kbskyblock.core.utils.ItemStackUtils;
import com.kbskyblock.core.utils.Placeholder;
import com.kbskyblock.core.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UpgradesGUI<T extends Team, U extends KBSkyblockUser<T>> extends BackGUI {

    private final T team;
    private final KBSkyblockTeams<T, U> teams;
    private final Map<Integer, String> upgrades = new HashMap<>();

    public UpgradesGUI(T team, Player player, KBSkyblockTeams<T, U> teams) {
        super(teams.getInventories().upgradesGUI.background, player, teams.getInventories().backButton);
        this.team = team;
        this.teams = teams;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = teams.getInventories().upgradesGUI;
        Inventory inventory = Bukkit.createInventory(this, noItemGUI.size, StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        super.addContent(inventory);

        upgrades.clear();
        for (Map.Entry<String, Enhancement<?>> enhancementEntry : teams.getEnhancementList().entrySet()) {
            if (enhancementEntry.getValue().type != EnhancementType.UPGRADE) continue;
            upgrades.put(enhancementEntry.getValue().item.slot, enhancementEntry.getKey());
            TeamEnhancement teamEnhancement = teams.getTeamManager().getTeamEnhancement(team, enhancementEntry.getKey());
            EnhancementData currentData = enhancementEntry.getValue().levels.get(teamEnhancement.getLevel());
            EnhancementData nextData = enhancementEntry.getValue().levels.get(teamEnhancement.getLevel() + 1);
            int seconds = Math.max((int) (teamEnhancement.getRemainingTime() % 60), 0);
            int minutes = Math.max((int) ((teamEnhancement.getRemainingTime() % 3600) / 60), 0);
            int hours = Math.max((int) (teamEnhancement.getRemainingTime() / 3600), 0);
            String nextLevel = nextData == null ? teams.getMessages().nullPlaceholder : String.valueOf(teamEnhancement.getLevel() + 1);
            String cost = nextData == null ? teams.getMessages().nullPlaceholder : String.valueOf(nextData.money);
            String minLevel = nextData == null ? teams.getMessages().nullPlaceholder : String.valueOf(nextData.minLevel);
            List<Placeholder> placeholders = currentData == null ? new ArrayList<>() : new ArrayList<>(currentData.getPlaceholders());
            placeholders.addAll(Arrays.asList(
                    new Placeholder("timeremaining_hours", String.valueOf(hours)),
                    new Placeholder("timeremaining_minutes", String.valueOf(minutes)),
                    new Placeholder("timeremaining_seconds", String.valueOf(seconds)),
                    new Placeholder("current_level", String.valueOf(teamEnhancement.getLevel())),
                    new Placeholder("minLevel", minLevel),
                    new Placeholder("next_level", nextLevel),
                    new Placeholder("cost", cost),
                    new Placeholder("vault_cost", cost)
            ));

            if(nextData != null) {
                for (Map.Entry<String, Double> bankItem : nextData.bankCosts.entrySet()) {
                    placeholders.add(new Placeholder(bankItem.getKey() + "_cost", formatPrice(bankItem.getValue())));
                }
            }

            inventory.setItem(enhancementEntry.getValue().item.slot, ItemStackUtils.makeItem(enhancementEntry.getValue().item, placeholders));
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event);

        if (!upgrades.containsKey(event.getSlot())) return;
        String upgrade = upgrades.get(event.getSlot());
        teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().upgradesCommand, new String[]{"buy", upgrade});
    }

    public String formatPrice(double value) {
        if (teams.getShop().abbreviatePrices) {
            return teams.getConfiguration().numberFormatter.format(value);
        }
        return String.valueOf(value);
    }
}
