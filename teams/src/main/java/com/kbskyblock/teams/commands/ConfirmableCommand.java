package com.kbskyblock.teams.commands;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.gui.ConfirmationGUI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ConfirmableCommand<T extends Team, U extends KBSkyblockUser<T>> extends Command<T, U> {
    public final boolean requiresConfirmation;

    public ConfirmableCommand() {
        super();
        this.requiresConfirmation = true;
    }

    public ConfirmableCommand(@NotNull List<String> aliases, @NotNull String description, @NotNull String syntax,
                              @NotNull String permission, long cooldownInSeconds, boolean requiresConfirmation) {
        super(aliases, description, syntax, permission, cooldownInSeconds);
        this.requiresConfirmation = requiresConfirmation;
    }

    @Override
    public final boolean execute(U user, T team, String[] arguments, KBSkyblockTeams<T, U> teams) {
        if (!isCommandValid(user, team, arguments, teams)) {
            return false;
        }

        if (requiresConfirmation) {
            Player player = user.getPlayer();

            player.openInventory(new ConfirmationGUI<>(() -> {
                executeAfterConfirmation(user, team, arguments, teams);
            }, teams).getInventory());
            return true;
        }

        executeAfterConfirmation(user, team, arguments, teams);
        return true;
    }

    protected abstract boolean isCommandValid(U user, T team, String[] arguments, KBSkyblockTeams<T, U> teams);

    protected abstract void executeAfterConfirmation(U user, T team, String[] arguments, KBSkyblockTeams<T, U> teams);
}