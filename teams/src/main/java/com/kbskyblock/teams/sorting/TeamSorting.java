package com.kbskyblock.teams.sorting;

import com.kbskyblock.core.Item;
import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public abstract class TeamSorting<T extends Team> {

    public Item item;
    public boolean enabled;

    public abstract List<T> getSortedTeams(KBSkyblockTeams<T, ?> teams);

    public abstract String getName();
    public abstract double getValue(T team);

    public int getRank(T team, KBSkyblockTeams<T, ?> teams) {
        List<T> teams = getSortedTeams(teams);
        return teams.indexOf(team) + 1;
    }
}
