package com.kbskyblock.teams;

import com.kbskyblock.core.utils.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public interface TeamChatPlaceholderBuilder {
    List<Placeholder> getPlaceholders(AsyncPlayerChatEvent event, Player player);
}
