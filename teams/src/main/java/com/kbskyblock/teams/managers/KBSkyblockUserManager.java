package com.kbskyblock.teams.managers
s;

import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KBSkyblockUserManager<T extends Team, U extends KBSkyblockUser<T>> {

    @NotNull U getUser(@NotNull OfflinePlayer offlinePlayer);

    Optional<U> getUserByUUID(@NotNull UUID uuid);

    List<U> getUsers();
}
