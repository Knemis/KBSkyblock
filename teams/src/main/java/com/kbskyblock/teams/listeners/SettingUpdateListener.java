package com.kbskyblock.teams.listeners;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.SettingType;
import com.kbskyblock.teams.api.SettingUpdateEvent;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

@AllArgsConstructor
public class SettingUpdateListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler
    public void onSettingUpdate(SettingUpdateEvent<T, U> event) {
        if (event.getSetting().equalsIgnoreCase(SettingType.TIME.getSettingKey())) {
            teams.getTeamManager().getTeamMembers(event.getTeam()).stream().map(U::getPlayer).filter(Objects::nonNull).forEach(player ->
                    teams.getTeamManager().sendTeamTime(player)
            );
        }
        if (event.getSetting().equalsIgnoreCase(SettingType.WEATHER.getSettingKey())) {
            teams.getTeamManager().getTeamMembers(event.getTeam()).stream().map(U::getPlayer).filter(Objects::nonNull).forEach(player ->
                    teams.getTeamManager().sendTeamWeather(player)
            );
        }
    }

}
