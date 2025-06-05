package com.kbskyblock.teams.gui;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.configs.inventories.NoItemGUI;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamInvite;
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

public class InvitesGUI<T extends Team, U extends KBSkyblockUser<T>> extends PagedGUI<TeamInvite> {

    private final T team;
    private final KBSkyblockTeams<T, U> teams;

    public InvitesGUI(T team, Player player, KBSkyblockTeams<T, U> teams) {
        super(
                1,
                teams.getInventories().invitesGUI.size,
                teams.getInventories().invitesGUI.background,
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
        NoItemGUI noItemGUI = teams.getInventories().invitesGUI;
        Inventory inventory = Bukkit.createInventory(this, getSize(), StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public Collection<TeamInvite> getPageObjects() {
        return teams.getTeamManager().getTeamInvites(team);
    }

    @Override
    public ItemStack getItemStack(TeamInvite teamInvite) {
        Optional<U> user = teams.getUserManager().getUserByUUID(teamInvite.getUser());
        List<Placeholder> placeholderList = new ArrayList<>(teams.getUserPlaceholderBuilder().getPlaceholders(user));
        placeholderList.add(new Placeholder("invite_time", teamInvite.getTime().format(DateTimeFormatter.ofPattern(teams.getConfiguration().dateTimeFormat))));
        return ItemStackUtils.makeItem(teams.getInventories().invitesGUI.item, placeholderList);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event);

        TeamInvite teamInvite = getItem(event.getSlot());
        if (teamInvite == null) return;

        String username = teams.getUserManager().getUserByUUID(teamInvite.getUser()).map(U::getName).orElse(teams.getMessages().nullPlaceholder);
        teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().unInviteCommand, new String[]{username});
    }
}
