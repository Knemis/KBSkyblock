package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.bank.BankItem;
import com.kbskyblock.teams.bank.BankResponse;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamBank;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public class DepositCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public DepositCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (args.length != 2) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }
        Optional<BankItem> bankItem = teams.getBankItemList().stream().filter(item -> item.getName().equalsIgnoreCase(args[0])).findFirst();
        if (!bankItem.isPresent()) {
            player.sendMessage(StringUtils.color(teams.getMessages().noSuchBankItem.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }

        try {
            TeamBank teamBank = teams.getTeamManager().getTeamBank(team, bankItem.get().getName());
            BankResponse bankResponse = bankItem.get().deposit(player, Double.parseDouble(args[1]), teamBank, teams);

            player.sendMessage(StringUtils.color((bankResponse.isSuccess() ? teams.getMessages().bankDeposited : teams.getMessages().insufficientFundsToDeposit)
                    .replace("%prefix%", teams.getConfiguration().prefix)
                    .replace("%amount%", String.valueOf(bankResponse.getAmount()))
                    .replace("%type%", bankItem.get().getName())
            ));
            return true;
        } catch (NumberFormatException exception) {
            player.sendMessage(StringUtils.color(teams.getMessages().notANumber.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args, KBSkyblockTeams<T, U> teams) {
        if (args.length == 1) {
            return teams.getBankItemList().stream()
                    .map(BankItem::getName)
                    .collect(Collectors.toList());
        }
        return Arrays.asList("100", "1000", "10000", "100000");
    }

}
