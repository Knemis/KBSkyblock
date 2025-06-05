package com.kbskyblock.teams.gui;

import com.cryptomorin.xseries.XMaterial;
import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.configs.BlockValues;
import com.kbskyblock.teams.configs.inventories.NoItemGUI;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.gui.PagedGUI;
import com.kbskyblock.core.utils.ItemStackUtils;
import com.kbskyblock.core.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpawnerValueGUI<T extends Team, U extends KBSkyblockUser<T>> extends PagedGUI<BlockValues.ValuableBlock> {

    private final T team;
    private final KBSkyblockTeams<T, U> teams;

    public SpawnerValueGUI(T team, Player player, KBSkyblockTeams<T, U> teams) {
        super(
                1,
                teams.getInventories().spawnerValueGUI.size,
                teams.getInventories().spawnerValueGUI.background,
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
        int maxPages = getPageObjects().size() / (getSize() - 9);
        if (getPageObjects().size() % (getSize() - 9) > 0) maxPages++;

        NoItemGUI noItemGUI = teams.getInventories().spawnerValueGUI;
        Inventory inventory = Bukkit.createInventory(this, getSize(), StringUtils.color(noItemGUI.title
                .replace("%page%", String.valueOf(getPage()))
                .replace("%max_pages%", String.valueOf(maxPages))
        ));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        super.addContent(inventory);
        for (Map.Entry<EntityType, BlockValues.ValuableBlock> entry : teams.getBlockValues().spawnerValues.entrySet().stream().filter(entry -> entry.getValue().page == getPage()).collect(Collectors.toList())) {

            List<String> lore = new ArrayList<>();
            lore.add(teams.getBlockValues().valueLore
                    .replace("%block_value%", String.valueOf(entry.getValue().value))
            );
            lore.add(teams.getBlockValues().teamValueLore
                    .replace("%total_blocks%", String.valueOf(teams.getTeamManager().getTeamSpawners(team, entry.getKey()).getAmount()))
                    .replace("%total_block_value%", String.valueOf(teams.getTeamManager().getTeamSpawners(team, entry.getKey()).getAmount() * entry.getValue().value))
            );

            String itemName = entry.getKey().name().toUpperCase() + "_SPAWN_EGG";
            XMaterial item = XMaterial.matchXMaterial(itemName).orElse(XMaterial.SPAWNER);

            inventory.setItem(entry.getValue().slot, ItemStackUtils.makeItem(item, 1, entry.getValue().name, lore));
        }
    }

    @Override
    public Collection<BlockValues.ValuableBlock> getPageObjects() {
        return teams.getBlockValues().spawnerValues.values();
    }

    @Override
    public ItemStack getItemStack(BlockValues.ValuableBlock valuableBlock) {
        return null;
    }

}
