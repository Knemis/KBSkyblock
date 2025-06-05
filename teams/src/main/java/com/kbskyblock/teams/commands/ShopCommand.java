package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.gui.ShopCategoryGUI;
import com.kbskyblock.teams.gui.ShopOverviewGUI;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class ShopCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {

    public ShopCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, String[] arguments, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (arguments.length == 0) {
            player.openInventory(new ShopOverviewGUI<>(player, teams).getInventory());
            return true;
        }

        Optional<String> categoryName = getCategoryName(String.join(" ", arguments), teams);

        if (!categoryName.isPresent()) {
            player.sendMessage(StringUtils.color(teams.getMessages().noShopCategory
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }

        player.openInventory(new ShopCategoryGUI<>(categoryName.get(), player, 1, teams).getInventory());
        return true;
    }

    private Optional<String> getCategoryName(String name, KBSkyblockTeams<T, U> teams) {
        for (String category : teams.getShop().categories.keySet()) {
            if (category.equalsIgnoreCase(name)) {
                return Optional.of(category);
            }
        }
        return Optional.empty();
    }
}
