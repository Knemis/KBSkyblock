package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.gui.RewardsGUI;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;

@NoArgsConstructor
public class RewardsCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public RewardsCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] arguments, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        player.openInventory(new RewardsGUI<>(team, player, teams).getInventory());
        return true;
    }

}
