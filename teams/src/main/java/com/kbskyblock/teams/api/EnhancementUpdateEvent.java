package com.kbskyblock.teams.api;

import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class EnhancementUpdateEvent<T extends Team, U extends KBSkyblockUser<T>> extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private T team;
    private U user;
    private int nextLevel;
    private String enhancement;
    private boolean cancelled;

    public EnhancementUpdateEvent(T team, U user, int nextLevel, String enhancement) {
        this.team = team;
        this.user = user;
        this.nextLevel = nextLevel;
        this.enhancement = enhancement;
        this.cancelled = false;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}