package com.kbskyblock.teams.listeners;

import com.kbskyblock.teams.ChatType;
import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class PlayerChatListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        U user = teams.getUserManager().getUser(event.getPlayer());
        Optional<T> yourTeam = teams.getTeamManager().getTeamViaID(user.getTeamID());
        Optional<ChatType> chatType = teams.getChatTypes().stream()
                .filter(type -> type.getAliases().stream().anyMatch(s -> s.equalsIgnoreCase(user.getChatType())))
                .findFirst();
        if (!yourTeam.isPresent() || !chatType.isPresent()) return;
        List<Player> players = chatType.get().getPlayerChat().getPlayers(event.getPlayer().getPlayer());
        if (players == null) return;
        for (Player player : players) {
            if(player == null) return;
            player.sendMessage(StringUtils.color(StringUtils.processMultiplePlaceholders(teams.getMessages().chatFormat, teams.getTeamChatPlaceholderBuilder().getPlaceholders(event, player))));
        }
        event.getRecipients().clear();
    }

}
