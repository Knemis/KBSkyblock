package com.kbskyblock.teams.bank;

import com.kbskyblock.core.Item;
import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.TeamBank;
import lombok.NoArgsConstructor;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class MoneyBankItem extends BankItem {

    public MoneyBankItem(double defaultAmount, Item item) {
        super("money", item, defaultAmount, true);
    }

    @Override
    public BankResponse withdraw(Player player, Number amount, TeamBank teamBank, KBSkyblockTeams<?, ?> teams) {
        double money = Math.min(amount.doubleValue(), teamBank.getNumber());
        if (money > 0) {
            EconomyResponse economyResponse = teams.getEconomy().depositPlayer(player, money);
            if (economyResponse.type == EconomyResponse.ResponseType.SUCCESS) {
                teamBank.setNumber(teamBank.getNumber() - money);
                return new BankResponse(money, true);
            }
        }
        return new BankResponse(money, false);
    }

    @Override
    public BankResponse deposit(Player player, Number amount, TeamBank teamBank, KBSkyblockTeams<?, ?> teams) {
        double money = Math.min(amount.doubleValue(), teams.getEconomy().getBalance(player));
        if (money > 0) {
            EconomyResponse economyResponse = teams.getEconomy().withdrawPlayer(player, money);
            if (economyResponse.type == EconomyResponse.ResponseType.SUCCESS) {
                teamBank.setNumber(teamBank.getNumber() + money);
                return new BankResponse(money, true);
            }
        }
        return new BankResponse(money, false);
    }

}
