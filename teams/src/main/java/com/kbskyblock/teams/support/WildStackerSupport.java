package com.iridium.iridiumteams.support;

// import com.bgsoftware.wildstacker.api.WildStackerAPI; // Commented out
// import com.bgsoftware.wildstacker.api.objects.StackedBarrel; // Commented out
// import com.bgsoftware.wildstacker.api.objects.StackedSpawner; // Commented out
import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumteams.IridiumTeams;
import com.iridium.iridiumteams.database.IridiumUser;
import com.iridium.iridiumteams.database.Team;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

import java.util.*;
import java.util.stream.Collectors;

public class WildStackerSupport<T extends Team, U extends IridiumUser<T>> implements StackerSupport<T>, SpawnerSupport<T> {

    private final IridiumTeams<T, U> iridiumTeams;

    public WildStackerSupport(IridiumTeams<T, U> iridiumTeams) {
        this.iridiumTeams = iridiumTeams;
    }

    @Override
    public String supportProvider() {
        return "WildStacker (Temporarily Disabled)";
    }

    @Override
    public boolean isStackedBlock(Block block) {
        // return WildStackerAPI.getWildStacker().getSystemManager().isStackedBarrel(block); // Commented out
        return false; // Default behavior
    }

    @Override
    public boolean isStackedSpawner(Block block) {
        // return WildStackerAPI.getWildStacker().getSystemManager().isStackedSpawner(block); // Commented out
        return false; // Default behavior
    }

    private Object getStackedBlock(Block block) { // Changed StackedBarrel to Object
        // return WildStackerAPI.getWildStacker().getSystemManager().getStackedBarrel(block); // Commented out
        return null; // Default behavior
    }

    private Object getStackedSpawner(Block block) { // Changed StackedSpawner to Object
        // return WildStackerAPI.getWildStacker().getSystemManager().getStackedSpawner(block.getLocation()); // Commented out
        return null; // Default behavior
    }

    private List<?> getStackedBarrels(List<Block> blocks) { // Changed List<StackedBarrel> to List<?>
        List<Object> stackedBarrels = new ArrayList<>(Collections.emptyList()); // Changed StackedBarrel to Object
        // for (Block b : blocks) { // Renamed block to b
        //     stackedBarrels.add(getStackedBlock(b));
        // }
        return stackedBarrels;
    }

    private List<?> getStackedSpawners(List<CreatureSpawner> spawners) { // Changed List<StackedSpawner> to List<?>
        List<Object> stackedSpawners = new ArrayList<>(Collections.emptyList()); // Changed StackedSpawner to Object
        // for (CreatureSpawner spawner : spawners) {
        //     stackedSpawners.add(getStackedSpawner(spawner.getBlock()));
        // }
        return stackedSpawners;
    }

    @Override
    public int getStackAmount(Block block) {
        // return WildStackerAPI.getWildStacker().getSystemManager().getStackedBarrel(block).getStackAmount(); // Commented out
        return 1; // Default behavior
    }

    @Override
    public int getStackAmount(CreatureSpawner spawner) {
        // return getStackedSpawner(spawner.getBlock()).getStackAmount(); // Commented out
        return 1; // Default behavior
    }

    @Override
    public int getSpawnAmount(CreatureSpawner spawner) {
        // return getStackAmount(spawner) * WildStackerAPI.getWildStacker().getSystemManager().getStackedSpawner(spawner).getSpawner().getSpawnCount(); // Commented out
        return WildStackerAPI_getSpawnCount_replacement(spawner); // Placeholder for original logic attempt
    }

    // Helper method to encapsulate the logic from getSpawnAmount if needed, or simplify
    private int WildStackerAPI_getSpawnCount_replacement(CreatureSpawner spawner) {
        // This is a placeholder. The original logic was:
        // getStackAmount(spawner) * WildStackerAPI.getWildStacker().getSystemManager().getStackedSpawner(spawner).getSpawner().getSpawnCount();
        // Since WildStackerAPI is commented out, we return a default or simplified value.
        // If getStackAmount(spawner) is now returning 1 (default), this might be 1 * some_default_spawn_count.
        // For now, returning a value that indicates it's not the original calculation.
        return getStackAmount(spawner) * 4; // Assuming a typical default spawn count of 4 from spigot.yml
    }


    @Override
    public Map<XMaterial, Integer> getBlocksStacked(Chunk chunk, T team) {
        HashMap<XMaterial, Integer> hashMap = new HashMap<>();
        // WildStackerAPI.getWildStacker().getSystemManager().getStackedBarrels(chunk).forEach(stackedBarrel -> { // Commented out
        //     if (!iridiumTeams.getTeamManager().isInTeam(team, stackedBarrel.getLocation())) return;
        //     XMaterial xMaterial = XMaterial.matchXMaterial(stackedBarrel.getType());
        //     hashMap.put(xMaterial, hashMap.getOrDefault(xMaterial, 0) + stackedBarrel.getStackAmount());
        // });
        return hashMap;
    }

    @Override
    public List<CreatureSpawner> getSpawnersStacked(Chunk chunk) {
        // return WildStackerAPI.getWildStacker().getSystemManager().getStackedSpawners(chunk).stream().map(StackedSpawner::getSpawner).collect(Collectors.toList()); // Commented out
        return new ArrayList<>(); // Return empty list
    }

    @Override
    public int getExtraBlocks(T team, XMaterial material, List<Block> blocks) {
        int stackedBlocks = 0;
        // for (StackedBarrel stackedBarrel : getStackedBarrels(blocks)) { // Commented out
        //     if (!iridiumTeams.getTeamManager().isInTeam(team, stackedBarrel.getLocation())) continue;
        //     if (material != XMaterial.matchXMaterial(stackedBarrel.getType())) continue;
        //     stackedBlocks += stackedBarrel.getStackAmount();
        // }
        return stackedBlocks;
    }

    @Override
    public int getExtraSpawners(T team, EntityType entityType, List<CreatureSpawner> spawners) {
        int stackedSpawners = 0;
        // for (StackedSpawner stackedSpawner : getStackedSpawners(spawners)) { // Commented out
        //     if (!iridiumTeams.getTeamManager().isInTeam(team, stackedSpawner.getLocation())) continue;
        //     if (stackedSpawner.getSpawnedType() != entityType) continue;
        //     stackedSpawners += stackedSpawner.getStackAmount();
        // }
        return stackedSpawners;
    }
}