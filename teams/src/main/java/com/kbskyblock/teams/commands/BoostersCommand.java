package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.enhancements.Enhancement;
import com.kbskyblock.teams.enhancements.EnhancementType;
import com.kbskyblock.teams.gui.BoostersGUI;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;

@NoArgsConstructor
public class BoostersCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public BoostersCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (args.length == 0) {
            player.openInventory(new BoostersGUI<>(team, player, teams).getInventory());
            return false;
        }
        if (args.length != 2 || !args[0].equalsIgnoreCase("buy")) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }
        String booster = args[1];
        Enhancement<?> enhancement = teams.getEnhancementList().get(booster);
        if (enhancement == null || enhancement.type != EnhancementType.BOOSTER) {
            player.sendMessage(StringUtils.color(teams.getMessages().noSuchBooster
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        boolean success = teams.getTeamManager().UpdateEnhancement(team, booster, player);
        if (success) {
            player.sendMessage(StringUtils.color(teams.getMessages().purchasedBooster
                    .replace("%prefix%", teams.getConfiguration().prefix)
                    .replace("%booster%", booster)
            ));
        }
        return success;
    }

}
