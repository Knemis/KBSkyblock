package com.kbskyblock.teams.listeners;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.Reward;
import com.kbskyblock.teams.api.TeamLevelUpEvent;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamReward;
import com.kbskyblock.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TeamLevelUpListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler(ignoreCancelled = true)
    public void onTeamLevelUp(TeamLevelUpEvent<T, U> event) {
        for (U member : teams.getTeamManager().getTeamMembers(event.getTeam())) {
            Player player = member.getPlayer();
            if(player == null) return;
            player.sendMessage(StringUtils.color(teams.getMessages().teamLevelUp
                    .replace("%prefix%", teams.getConfiguration().prefix)
                    .replace("%level%", String.valueOf(event.getTeam().getLevel()))
            ));
        }

        if (event.isFirstTimeAsLevel() && event.getLevel() > 1) {
            if(!teams.getConfiguration().giveLevelRewards) return;
            Reward reward = null;
            List<Map.Entry<Integer, Reward>> entries = teams.getConfiguration().levelRewards.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toList());
            for (Map.Entry<Integer, Reward> entry : entries) {
                if (event.getLevel() % entry.getKey() == 0) {
                    reward = entry.getValue();
                }
            }
            if (reward != null) {
                reward.item.lore = StringUtils.processMultiplePlaceholders(reward.item.lore, teams.getTeamsPlaceholderBuilder().getPlaceholders(event.getTeam()));
                reward.item.displayName = StringUtils.processMultiplePlaceholders(reward.item.displayName, teams.getTeamsPlaceholderBuilder().getPlaceholders(event.getTeam()));
                teams.getTeamManager().addTeamReward(new TeamReward(event.getTeam(), reward));
            }
        }
    }
}
