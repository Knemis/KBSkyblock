package com.kbskyblock.teams.gui;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.configs.inventories.NoItemGUI;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamTrust;
import com.kbskyblock.core.gui.PagedGUI;
import com.kbskyblock.core.utils.ItemStackUtils;
import com.kbskyblock.core.utils.Placeholder;
import com.kbskyblock.core.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class TrustsGUI<T extends Team, U extends KBSkyblockUser<T>> extends PagedGUI<TeamTrust> {

    private final T team;
    private final KBSkyblockTeams<T, U> teams;

    public TrustsGUI(T team, Player player, KBSkyblockTeams<T, U> teams) {
        super(
                1,
                teams.getInventories().trustsGUI.size,
                teams.getInventories().trustsGUI.background,
                teams.getInventories().previousPage,
                teams.getInventories().nextPage,
                player,
                teams.getInventories().backButton
        );
        this.team = team;
        this.teams = teams;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = teams.getInventories().trustsGUI;
        Inventory inventory = Bukkit.createInventory(this, getSize(), StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public Collection<TeamTrust> getPageObjects() {
        return teams.getTeamManager().getTeamTrusts(team);
    }

    @Override
    public ItemStack getItemStack(TeamTrust teamTrust) {
        Optional<U> user = teams.getUserManager().getUserByUUID(teamTrust.getUser());
        Optional<U> truster = teams.getUserManager().getUserByUUID(teamTrust.getTruster());
        List<Placeholder> placeholderList = new ArrayList<>(teams.getUserPlaceholderBuilder().getPlaceholders(user));
        placeholderList.add(new Placeholder("trusted_time", teamTrust.getTime().format(DateTimeFormatter.ofPattern(teams.getConfiguration().dateTimeFormat))));
        placeholderList.add(new Placeholder("truster", truster.map(U::getName).orElse(teams.getMessages().nullPlaceholder)));
        return ItemStackUtils.makeItem(teams.getInventories().trustsGUI.item, placeholderList);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event);

        TeamTrust teamTrust = getItem(event.getSlot());
        if (teamTrust == null) return;

        String username = teams.getUserManager().getUserByUUID(teamTrust.getUser()).map(U::getName).orElse(teams.getMessages().nullPlaceholder);
        teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().unTrustCommand, new String[]{username});
    }
}
