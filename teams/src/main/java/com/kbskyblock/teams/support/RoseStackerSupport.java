package com.iridium.iridiumteams.support;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumteams.IridiumTeams;
import com.iridium.iridiumteams.database.IridiumUser;
import com.iridium.iridiumteams.database.Team;
// import dev.rosewood.rosestacker.api.RoseStackerAPI; // Commented out
// import dev.rosewood.rosestacker.stack.StackedBlock; // Commented out
// import dev.rosewood.rosestacker.stack.StackedSpawner; // Commented out
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

import java.util.*;
import java.util.stream.Collectors;

public class RoseStackerSupport<T extends Team, U extends IridiumUser<T>> implements StackerSupport<T>, SpawnerSupport<T> {

    private final IridiumTeams<T, U> iridiumTeams;

    public RoseStackerSupport(IridiumTeams<T, U> iridiumTeams) {
        this.iridiumTeams = iridiumTeams;
    }

    @Override
    public String supportProvider() {
        return "RoseStacker (Temporarily Disabled)";
    }

    @Override
    public boolean isStackedBlock(Block block) {
        // return RoseStackerAPI.getInstance().isBlockStacked(block); // Commented out
        return false; // Default behavior
    }

    @Override
    public boolean isStackedSpawner(Block block) {
        // return RoseStackerAPI.getInstance().isSpawnerStacked(block); // Commented out
        return false; // Default behavior
    }

    private Object getStackedBlock(Block block) { // Changed StackedBlock to Object
        // return RoseStackerAPI.getInstance().getStackedBlock(block); // Commented out
        return null; // Default behavior
    }

    private Object getStackedSpawner(Block block) { // Changed StackedSpawner to Object
        // return RoseStackerAPI.getInstance().getStackedSpawner(block); // Commented out
        return null; // Default behavior
    }

    private List<?> getStackedBlocks(List<Block> blocks) { // Changed List<StackedBlock> to List<?>
        List<Object> stackedBlocks = new ArrayList<>(Collections.emptyList()); // Changed StackedBlock to Object
        // for(Block b : blocks) { // Renamed block to b
        //     stackedBlocks.add(getStackedBlock(b));
        // }
        return stackedBlocks;
    }

    private List<?> getStackedSpawners(List<CreatureSpawner> spawners) { // Changed List<StackedSpawner> to List<?>
        List<Object> stackedSpawners = new ArrayList<>(Collections.emptyList()); // Changed StackedSpawner to Object
        // for(CreatureSpawner spawner : spawners) {
        //     stackedSpawners.add(getStackedSpawner(spawner.getBlock()));
        // }
        return stackedSpawners;
    }

    @Override
    public int getStackAmount(Block block) {
        // return getStackedBlock(block).getStackSize(); // Commented out
        return 1; // Default behavior
    }

    @Override
    public int getStackAmount(CreatureSpawner spawner) {
        // return getStackedSpawner(spawner.getBlock()).getStackSize(); // Commented out
        return 1; // Default behavior
    }

    @Override
    public int getSpawnAmount(CreatureSpawner spawner) {
        // return getStackAmount(spawner) * RoseStackerAPI.getInstance().getStackedSpawner(spawner.getBlock()).getSpawner().getSpawnCount(); // Commented out
        // Placeholder logic, similar to WildStacker
        return getStackAmount(spawner) * 4; // Assuming a typical default spawn count
    }

    @Override
    public Map<XMaterial, Integer> getBlocksStacked(Chunk chunk, T team) {
        HashMap<XMaterial, Integer> hashMap = new HashMap<>();
        // RoseStackerAPI.getInstance().getStackedBlocks(Collections.singletonList(chunk)).forEach(stackedBlock -> { // Commented out
        //     if(!iridiumTeams.getTeamManager().isInTeam(team, stackedBlock.getLocation())) return;
        //     XMaterial xMaterial = XMaterial.matchXMaterial(stackedBlock.getStackSettings().getType());
        //     hashMap.put(xMaterial, hashMap.getOrDefault(xMaterial, 0) + stackedBlock.getStackSize());
        // });
        return hashMap;
    }

    @Override
    public List<CreatureSpawner> getSpawnersStacked(Chunk chunk) {
        // return RoseStackerAPI.getInstance().getStackedSpawners(Collections.singletonList(chunk)).stream().map(StackedSpawner::getSpawner).collect(Collectors.toList()); // Commented out
        return new ArrayList<>(); // Return empty list
    }

    @Override
    public int getExtraBlocks(T team, XMaterial material, List<Block> blocks) {
        int stackedBlocks = 0;
        // for (StackedBlock stackedBlock : getStackedBlocks(blocks)) { // Commented out
        //     if (!iridiumTeams.getTeamManager().isInTeam(team, stackedBlock.getLocation())) continue;
        //     if (material != XMaterial.matchXMaterial(stackedBlock.getBlock().getType())) continue;
        //     stackedBlocks += (stackedBlock.getStackSize() - 1);
        // }
        return stackedBlocks;
    }

    @Override
    public int getExtraSpawners(T team, EntityType entityType, List<CreatureSpawner> spawners) {
        int stackedSpawners = 0;
        // for (StackedSpawner stackedSpawner : getStackedSpawners(spawners)) { // Commented out
        //     if (!iridiumTeams.getTeamManager().isInTeam(team, stackedSpawner.getLocation())) continue;
        //     if (stackedSpawner.getSpawner().getSpawnedType() != entityType) continue;
        //     stackedSpawners += (stackedSpawner.getStackSize() - 1);
        // }
        return stackedSpawners;
    }
}