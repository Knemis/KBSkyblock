package com.kbskyblock.teams.placeholders;

import com.kbskyblock.core.utils.Placeholder;
import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.managers.TeamManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Placeholders<T extends Team, U extends KBSkyblockUser<T>> {
    private final KBSkyblockTeams<T, U> teams;

    public Placeholders(KBSkyblockTeams<T, U> teams) {
        this.teams = teams;
    }

    public List<Placeholder> getDefaultPlaceholders() {
        return teams.getTeamsPlaceholderBuilder().getPlaceholders(Optional.empty());
    }

    public List<Placeholder> getPlaceholders(@Nullable Player player) {
        U user = player == null ? null : teams.getUserManager().getUser(player);
        Optional<T> team = user == null ? Optional.empty() : teams.getTeamManager().getTeamViaID(user.getTeamID());
        Optional<T> current = user == null ? Optional.empty() : teams.getTeamManager().getTeamViaPlayerLocation(player);
        List<T> topValue = teams.getTeamManager().getTeams(TeamManager.SortType.Value, true);
        List<T> topExperience = teams.getTeamManager().getTeams(TeamManager.SortType.Experience, true);

        List<Placeholder> placeholders = new ArrayList<>();

        placeholders.addAll(teams.getTeamsPlaceholderBuilder().getPlaceholders(team));
        placeholders.addAll(teams.getUserPlaceholderBuilder().getPlaceholders(Optional.ofNullable(user)));
        for (Placeholder placeholder : teams.getTeamsPlaceholderBuilder().getPlaceholders(current)) {
            placeholders.add(new Placeholder("current_" + formatPlaceholderKey(placeholder.getKey()), placeholder.getValue()));
        }

        for (int i = 1; i <= 20; i++) {
            Optional<T> value = topValue.size() >= i ? Optional.of(topValue.get(i - 1)) : Optional.empty();
            Optional<T> experience = topExperience.size() >= i ? Optional.of(topExperience.get(i - 1)) : Optional.empty();
            for (Placeholder placeholder : teams.getTeamsPlaceholderBuilder().getPlaceholders(value)) {
                placeholders.add(new Placeholder("top_value_" + i + "_" + formatPlaceholderKey(placeholder.getKey()), placeholder.getValue()));
            }
            for (Placeholder placeholder : teams.getTeamsPlaceholderBuilder().getPlaceholders(experience)) {
                placeholders.add(new Placeholder("top_experience_" + i + "_" + formatPlaceholderKey(placeholder.getKey()), placeholder.getValue()));
            }
        }

        return placeholders;
    }

    private String formatPlaceholderKey(String key) {
        return key.replace("%", "");
    }

}
