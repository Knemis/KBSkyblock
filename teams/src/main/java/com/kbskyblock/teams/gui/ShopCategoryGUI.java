package com.kbskyblock.teams.gui;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.configs.Shop;
import com.kbskyblock.teams.configs.inventories.NoItemGUI;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.gui.BackGUI;
import com.kbskyblock.core.utils.ItemStackUtils;
import com.kbskyblock.core.utils.Placeholder;
import com.kbskyblock.core.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ShopCategoryGUI<T extends Team, U extends KBSkyblockUser<T>> extends BackGUI {
    private final KBSkyblockTeams<T, U> teams;
    @Getter
    private final String categoryName;
    private final Shop.ShopCategory shopCategory;
    @Getter
    private int page;

    public ShopCategoryGUI(String categoryName, Player player, int page, KBSkyblockTeams<T, U> teams) {
        super(teams.getInventories().shopCategoryGUI.background, player, teams.getInventories().backButton);
        this.teams = teams;
        this.categoryName = categoryName;
        this.shopCategory = teams.getShop().categories.get(categoryName);
        this.page = page;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = teams.getInventories().shopOverviewGUI;
        Inventory inventory = Bukkit.createInventory(this, shopCategory.inventorySize, StringUtils.color(noItemGUI.title.replace("%category_name%", categoryName)));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        super.addContent(inventory);

        if (!teams.getShop().items.containsKey(categoryName)) {
            teams.getLogger().warning("Shop Category " + categoryName + " Is not configured with any items!");
            return;
        }

        for (Shop.ShopItem shopItem : teams.getShop().items.get(categoryName)) {
            if (shopItem.page != this.page) continue;
            ItemStack itemStack = shopItem.type.parseItem();
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemStack.setAmount(shopItem.defaultAmount);
            itemMeta.setDisplayName(StringUtils.color(shopItem.name));
            itemMeta.setLore(getShopLore(shopItem));

            itemStack.setItemMeta(itemMeta);
            inventory.setItem(shopItem.slot, itemStack);
        }

        inventory.setItem(inventory.getSize() - 3, ItemStackUtils.makeItem(this.teams.getInventories().nextPage));
        inventory.setItem(inventory.getSize() - 7, ItemStackUtils.makeItem(this.teams.getInventories().previousPage));
    }

    private List<Placeholder> getShopLorePlaceholders(Shop.ShopItem item) {
        List<Placeholder> placeholders = new ArrayList<>(Arrays.asList(
                new Placeholder("amount", teams.getShopManager().formatPrice(item.defaultAmount)),
                new Placeholder("vault_cost", teams.getShopManager().formatPrice(item.buyCost.money)),
                new Placeholder("vault_reward", teams.getShopManager().formatPrice(item.sellCost.money)),
                new Placeholder("minLevel", String.valueOf(item.minLevel))
        ));
        for (Map.Entry<String, Double> bankItem : item.buyCost.bankItems.entrySet()) {
            placeholders.add(new Placeholder(bankItem.getKey() + "_cost", teams.getShopManager().formatPrice(bankItem.getValue())));
        }
        for (Map.Entry<String, Double> bankItem : item.sellCost.bankItems.entrySet()) {
            placeholders.add(new Placeholder(bankItem.getKey() + "_reward", teams.getShopManager().formatPrice(bankItem.getValue())));
        }
        return placeholders;
    }

    private List<String> getShopLore(Shop.ShopItem item) {
        List<String> lore = item.lore == null ? new ArrayList<>() : new ArrayList<>(StringUtils.color(item.lore));
        List<Placeholder> placeholders = getShopLorePlaceholders(item);

        if (item.buyCost.canPurchase()) {
            lore.add(teams.getShop().buyPriceLore);
        } else {
            lore.add(teams.getShop().notPurchasableLore);
        }

        if(item.minLevel > 1) {
            lore.add(teams.getShop().levelRequirementLore);
        }

        if (item.sellCost.canPurchase()) {
            lore.add(teams.getShop().sellRewardLore);
        } else {
            lore.add(teams.getShop().notSellableLore);
        }

        lore.addAll(teams.getShop().shopItemLore);

        return StringUtils.color(StringUtils.processMultiplePlaceholders(lore, placeholders));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event);

        if (event.getSlot() == event.getInventory().getSize() - 3 && doesNextPageExist()) {
            this.page++;
            addContent(event.getInventory());
            return;
        }

        if (event.getSlot() == event.getInventory().getSize() - 7 && doesPreviousPageExist()) {
            this.page--;
            addContent(event.getInventory());
            return;
        }

        Optional<Shop.ShopItem> shopItem = teams.getShop().items.get(categoryName).stream()
                .filter(item -> item.slot == event.getSlot())
                .filter(item -> item.page == this.page)
                .findAny();

        if (!shopItem.isPresent()) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int amount = event.isShiftClick() ? shopItem.get().type.parseItem().getMaxStackSize() : shopItem.get().defaultAmount;
        if (event.isLeftClick() && shopItem.get().buyCost.canPurchase()) {
            teams.getShopManager().buy(player, shopItem.get(), amount);
        } else if (event.isRightClick() && shopItem.get().sellCost.canPurchase()) {
            teams.getShopManager().sell(player, shopItem.get(), amount);
        } else {
            teams.getShop().failSound.play(player);
        }
    }

    private boolean doesNextPageExist() {
        return teams.getShop().items.get(categoryName).stream().anyMatch(item -> item.page == this.page + 1);
    }

    private boolean doesPreviousPageExist() {
        return teams.getShop().items.get(categoryName).stream().anyMatch(item -> item.page == this.page - 1);
    }
}
