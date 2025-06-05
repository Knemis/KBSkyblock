package com.kbskyblock.teams.gui;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.configs.inventories.NoItemGUI;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamReward;
import com.kbskyblock.core.Item;
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

public class RewardsGUI<T extends Team, U extends KBSkyblockUser<T>> extends PagedGUI<TeamReward> {

    private final KBSkyblockTeams<T, U> teams;
    private final T team;

    public RewardsGUI(T team, Player player, KBSkyblockTeams<T, U> teams) {
        super(
                1,
                teams.getInventories().rewardsGUI.size,
                teams.getInventories().rewardsGUI.background,
                teams.getInventories().previousPage,
                teams.getInventories().nextPage,
                player,
                teams.getInventories().backButton
        );
        this.teams = teams;
        this.team = team;
    }

    @Override
    public void addContent(Inventory inventory) {
        super.addContent(inventory);
        Item item = teams.getInventories().rewardsGUI.item;
        inventory.setItem(item.slot, ItemStackUtils.makeItem(item));
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = teams.getInventories().rewardsGUI;
        Inventory inventory = Bukkit.createInventory(this, getSize(), StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public Collection<TeamReward> getPageObjects() {
        return teams.getTeamManager().getTeamRewards(team);
    }

    @Override
    public ItemStack getItemStack(TeamReward teamReward) {
        return ItemStackUtils.makeItem(teamReward.getReward().item);
    }

    @Override
    public boolean isPaged() {
        return true;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event);

        if(event.getSlot() == teams.getInventories().rewardsGUI.item.slot){
            for(TeamReward teamReward : getPageObjects()){
                teams.getTeamManager().claimTeamReward(teamReward, (Player) event.getWhoClicked());
            }
            return;
        }

        TeamReward teamReward = getItem(event.getSlot());
        if (teamReward == null) return;
        teams.getTeamManager().claimTeamReward(teamReward, (Player) event.getWhoClicked());
    }
}
