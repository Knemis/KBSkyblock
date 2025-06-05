package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.Rank;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.gui.ConfirmationGUI;
import com.kbskyblock.core.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class TransferCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public TransferCommand(List<String> args, String description, String syntax, String permission, long cooldownInSeconds) {
        super(args, description, syntax, permission, cooldownInSeconds);
    }

    @Override
    public boolean execute(U user, T team, String[] args, KBSkyblockTeams<T, U> teams) {
        Player player = user.getPlayer();
        if (args.length != 1) {
            player.sendMessage(StringUtils.color(syntax.replace("%prefix%", teams.getConfiguration().prefix)));
            return false;
        }
        if (user.getUserRank() != Rank.OWNER.getId() && !user.isBypassing()) {
            player.sendMessage(StringUtils.color(teams.getMessages().mustBeOwnerToTransfer
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            player.sendMessage(StringUtils.color(teams.getMessages().notAPlayer
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        U targetUser = teams.getUserManager().getUser(targetPlayer);
        if (targetUser.getTeamID() != team.getId()) {
            player.sendMessage(StringUtils.color(teams.getMessages().userNotInYourTeam
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }
        if (targetPlayer.getUniqueId().equals(player.getUniqueId()) && !user.isBypassing()) {
            player.sendMessage(StringUtils.color(teams.getMessages().cannotTransferToYourself
                    .replace("%prefix%", teams.getConfiguration().prefix)
            ));
            return false;
        }

        player.openInventory(new ConfirmationGUI<>(() -> {
            targetUser.setUserRank(Rank.OWNER.getId());
            teams.getTeamManager().getTeamMembers(team).forEach(user1 -> {
                if (user1.getUserRank() == Rank.OWNER.getId() && user1 != targetUser) {
                    user1.setUserRank(teams.getUserRanks().keySet().stream().max(Integer::compareTo).orElse(1));
                }
                Player p = user1.getPlayer();
                if (p != null) {
                    p.sendMessage(StringUtils.color(teams.getMessages().ownershipTransferred
                            .replace("%prefix%", teams.getConfiguration().prefix)
                            .replace("%old_owner%", user.getName())
                            .replace("%new_owner%", targetUser.getName())
                    ));
                }
            });
            getCooldownProvider().applyCooldown(player);
        }, teams).getInventory());
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] args, KBSkyblockTeams<T, U> teams) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

}
