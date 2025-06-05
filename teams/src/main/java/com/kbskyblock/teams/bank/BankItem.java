package com.kbskyblock.teams.bank;

import com.kbskyblock.core.Item;
import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.TeamBank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public abstract class BankItem {

    private String name;
    private Item item;
    private double defaultAmount;
    private boolean enabled;

    public abstract BankResponse withdraw(Player player, Number amount, TeamBank teamBank, KBSkyblockTeams<?, ?> teams);

    public abstract BankResponse deposit(Player player, Number amount, TeamBank teamBank, KBSkyblockTeams<?, ?> teams);

}
