package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamEnhancement;
import com.kbskyblock.teams.enhancements.Enhancement;
import com.kbskyblock.teams.enhancements.EnhancementType;
import com.kbskyblock.teams.gui.UpgradesGUI;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;

@NoArgsConstructor
public class UpgradesCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public UpgradesCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (args.length == 0) {
            player.openInventory(new UpgradesGUI<>(team, player, teams).getInventory());
            return true;
        }
        if (args.length != 2 || !args[0].equalsIgnoreCase("buy")) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }
        String booster = args[1];
        Enhancement<?> enhancement = teams.getEnhancementList().get(booster);
        if (enhancement == null || enhancement.type != EnhancementType.UPGRADE) {
            player.sendMessage(StringUtils.color(teams.getMessages().noSuchUpgrade
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        TeamEnhancement teamEnhancement = teams.getTeamManager().getTeamEnhancement(team, booster);
        if(enhancement.levels.get(teamEnhancement.getLevel() + 1) == null){
            player.sendMessage(StringUtils.color(teams.getMessages().maxUpgradeLevelReached
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        boolean success = teams.getTeamManager().UpdateEnhancement(team, booster, player);
        if (success) {
            player.sendMessage(StringUtils.color(teams.getMessages().purchasedUpgrade
                    .replace("%prefix%", teams.getConfiguration().prefix)
                    .replace("%upgrade%", booster)
            ));
        }
        return success;
    }
}
