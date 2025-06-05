package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.bank.BankItem;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamBank;
import com.kbskyblock.teams.gui.BankGUI;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public class BankCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public String adminPermission;

    public BankCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds, String adminPermission) {
        super(args, description, syntax, permission, cooldownInSeconds);
        this.adminPermission = adminPermission;
    }

    @Override
    public boolean execute(CommandSender sender, String[] arguments, KBSkyblockTeams<T, U> teams) {
        if (arguments.length == 4) {
            Optional<T> team = teams.getTeamManager().getTeamViaNameOrPlayer(arguments[1]);
            if (!team.isPresent()) {
                sender.sendMessage(StringUtils.color(teams.getMessages().teamDoesntExistByName
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                return false;
            }
            Optional<BankItem> bankItem = teams.getBankItemList().stream()
                    .filter(item -> item.getName().equalsIgnoreCase(arguments[2]))
                    .findAny();
            double amount;
            try {
                amount = Double.parseDouble(arguments[3]);
            } catch (NumberFormatException exception) {
                sender.sendMessage(StringUtils.color(teams.getMessages().notANumber
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                return false;
            }

            if (!sender.hasPermission(adminPermission)) {
                sender.sendMessage(StringUtils.color(teams.getMessages().noPermission
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                return false;
            }

            if (!bankItem.isPresent()) {
                sender.sendMessage(StringUtils.color(teams.getMessages().noSuchBankItem
                        .replace("%prefix%", teams.getConfiguration().prefix)
                ));
                return false;
            }
            TeamBank teamBank = teams.getTeamManager().getTeamBank(team.get(), bankItem.get().getName());
            switch (arguments[0].toLowerCase()) {
                case "give":
                    teamBank.setNumber(teamBank.getNumber() + amount);

                    sender.sendMessage(StringUtils.color(teams.getMessages().gaveBank
                            .replace("%prefix%", teams.getConfiguration().prefix)
                            .replace("%player%", arguments[1])
                            .replace("%amount%", String.valueOf(amount))
                            .replace("%item%", bankItem.get().getName())
                    ));
                    break;
                case "remove":
                    teamBank.setNumber(teamBank.getNumber() - amount);

                    sender.sendMessage(StringUtils.color(teams.getMessages().removedBank
                            .replace("%prefix%", teams.getConfiguration().prefix)
                            .replace("%player%", arguments[1])
                            .replace("%amount%", String.valueOf(amount))
                            .replace("%item%", bankItem.get().getName())
                    ));
                    break;
                case "set":
                    teamBank.setNumber(amount);

                    sender.sendMessage(StringUtils.color(teams.getMessages().setBank
                            .replace("%prefix%", teams.getConfiguration().prefix)
                            .replace("%player%", arguments[1])
                            .replace("%amount%", String.valueOf(amount))
                            .replace("%item%", bankItem.get().getName())
                    ));
                    break;
                default:
                    sender.sendMessage(StringUtils.color(syntax
                            .replace("%prefix%", teams.getConfiguration().prefix)
                    ));
            }
            return true;
        }
        if (arguments.length != 0) {
            sender.sendMessage(StringUtils.color(syntax
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        return super.execute(sender, arguments, teams);
    }

    @Override
    public boolean execute(U user, T team, String[] arguments, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        player.openInventory(new BankGUI<>(team, player, teams).getInventory());
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args, KBSkyblockTeams<T, U> teams) {
        if (!commandSender.hasPermission(adminPermission)) return Collections.emptyList();
        switch (args.length) {
            case 1:
                return Arrays.asList("give", "set", "remove");
            case 2:
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            case 3:
                return teams.getBankItemList().stream().map(BankItem::getName).collect(Collectors.toList());
            case 4:
                return Arrays.asList("1", "10", "100");
            default:
                return Collections.emptyList();
        }
    }
}
