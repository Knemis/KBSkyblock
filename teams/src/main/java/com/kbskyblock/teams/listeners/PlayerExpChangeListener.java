package com.kbskyblock.teams.listeners;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamEnhancement;
import com.kbskyblock.teams.enhancements.Enhancement;
import com.kbskyblock.teams.enhancements.ExperienceEnhancementData;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

@AllArgsConstructor
public class PlayerExpChangeListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    //Could cause dupe's of xp if they have a plugin to deposit xp
    @EventHandler(ignoreCancelled = true)
    public void onPlayerExperienceChange(PlayerExpChangeEvent event) {
        teams.getTeamManager().getTeamViaID(teams.getUserManager().getUser(event.getPlayer()).getTeamID()).ifPresent(team -> {
            Enhancement<ExperienceEnhancementData> spawnerEnhancement = teams.getEnhancements().experienceEnhancement;
            TeamEnhancement teamEnhancement = teams.getTeamManager().getTeamEnhancement(team, "experience");
            ExperienceEnhancementData data = spawnerEnhancement.levels.get(teamEnhancement.getLevel());

            if (!teamEnhancement.isActive(spawnerEnhancement.type)) return;
            if (data == null) return;

            event.setAmount((int) (event.getAmount() * data.experienceModifier));
        });
    }
}
