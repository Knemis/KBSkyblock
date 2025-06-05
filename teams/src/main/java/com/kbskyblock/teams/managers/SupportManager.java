package com.kbskyblock.teams.manager;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.HashSet;

public class SupportManager<T extends Team, U extends KBSkyblockUser<T>> {

    private final KBSkyblockTeams<T, U> teams;

    public SupportManager(KBSkyblockTeams<T, U> teams) {
        this.teams = teams;
    }

    @Getter
    private HashSet<StackerSupport<T>> stackerSupport = new HashSet<>();
    @Getter
    private HashSet<SpawnerSupport<T>> spawnerSupport = new HashSet<>();
    @Getter
    private HashSet<SpawnSupport<T>> spawnSupport = new HashSet<>();
    @Getter
    private HashSet<String> providerList = new HashSet<>();

    public boolean supportedPluginEnabled(String pluginName) {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }

    private void registerBlockStackerSupport() {
        if (supportedPluginEnabled("RoseStacker"))
            stackerSupport.add(new RoseStackerSupport<>(teams));

        if (supportedPluginEnabled("WildStacker"))
            stackerSupport.add(new WildStackerSupport<>(teams));

        if(supportedPluginEnabled("ObsidianStacker"))
            stackerSupport.add(new ObsidianStackerSupport<>(teams));
    }

    private void registerSpawnerSupport() {
        if (supportedPluginEnabled("RoseStacker"))
            spawnerSupport.add(new RoseStackerSupport<>(teams));

        if (supportedPluginEnabled("WildStacker"))
            spawnerSupport.add(new WildStackerSupport<>(teams));
    }

    private void registerSpawnSupport() {
        if (supportedPluginEnabled("EssentialsSpawn"))
            spawnSupport.add(new EssentialsSpawnSupport<>(teams));
    }

    public void registerSupport() {
        registerBlockStackerSupport();
        registerSpawnerSupport();
        registerSpawnSupport();

        stackerSupport.forEach(provider -> providerList.add(provider.supportProvider()));
        spawnerSupport.forEach(provider -> providerList.add(provider.supportProvider()));
        spawnSupport.forEach(provider -> providerList.add(provider.supportProvider()));
    }
}