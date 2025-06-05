package com.kbskyblock.teams.placeholders;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.Placeholder;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.util.List;

public class ClipPlaceholderAPI<T extends Team, U extends KBSkyblockUser<T>> extends PlaceholderExpansion {

    private final KBSkyblockTeams<T, U> teams;
    private final Placeholders<T, U> placeholders;

    public ClipPlaceholderAPI(KBSkyblockTeams<T, U> teams) {
        this.teams = teams;
        this.placeholders = new Placeholders<>(teams);
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return teams.getName().toLowerCase();
    }

    @Override
    public String getAuthor() {
        return "Peaches_MLG";
    }

    @Override
    public String getVersion() {
        return teams.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String placeholderKey) {
        List<Placeholder> placeholderList = placeholders.getPlaceholders(player);

        for (Placeholder placeholder : placeholderList) {
            if (formatPlaceholderKey(placeholder.getKey()).equalsIgnoreCase(placeholderKey)) return placeholder.getValue();
        }

        return null;
    }

    public int getPlaceholderCount(){
        return placeholders.getDefaultPlaceholders().size();
    }

    private String formatPlaceholderKey(String key){
        return key.replace("%", "");
    }
}
