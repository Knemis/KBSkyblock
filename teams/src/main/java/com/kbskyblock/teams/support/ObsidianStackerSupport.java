package com.iridium.iridiumteams.support;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumteams.IridiumTeams;
import com.iridium.iridiumteams.database.IridiumUser;
import com.iridium.iridiumteams.database.Team;
// import com.moyskleytech.obsidianstacker.api.Stack; // Commented out
// import com.moyskleytech.obsidianstacker.api.StackerAPI; // Commented out
import org.bukkit.Chunk;
import org.bukkit.block.Block;

import java.util.*;

public class ObsidianStackerSupport<T extends Team, U extends IridiumUser<T>> implements StackerSupport<T> {

    private final IridiumTeams<T, U> iridiumTeams;

    public ObsidianStackerSupport(IridiumTeams<T, U> iridiumTeams) {
        this.iridiumTeams = iridiumTeams;
    }

    @Override
    public String supportProvider() {
        return "ObsidianStacker (Temporarily Disabled)";
    }

    @Override
    public boolean isStackedBlock(Block block) {
        // Optional<Stack> stackedBlock = StackerAPI.getInstance().getStack(block); // Commented out
        // return stackedBlock.isPresent(); // Commented out
        return false; // Default behavior
    }

    private Object getStackedBlock(Block block) { // Changed Stack to Object
        // return StackerAPI.getInstance().getStack(block).get(); // Commented out
        return null; // Default behavior
    }

    private List<?> getStackedBlocks(List<Block> blocks) { // Changed List<Stack> to List<?>
        List<Object> stackedBlocks = new ArrayList<>(Collections.emptyList()); // Changed Stack to Object
        // for(Block b : blocks) { // Renamed block to b to avoid conflict if needed, or comment out
        //     stackedBlocks.add(getStackedBlock(b));
        // }
        return stackedBlocks;
    }

    @Override
    public int getStackAmount(Block block) {
        // return getStackedBlock(block).getCount(); // Commented out
        return 1; // Default behavior
    }

    @Override
    public Map<XMaterial, Integer> getBlocksStacked(Chunk chunk, T team) {
        HashMap<XMaterial, Integer> hashMap = new HashMap<>();
        // Logic involving StackerAPI would go here
        return hashMap; // Return empty map
    }

    @Override
    public int getExtraBlocks(T team, XMaterial material, List<Block> blocks) {
        int stackedBlocks = 0;
        // for (Stack stack : getStackedBlocks(blocks)) { // Commented out
        //     if (!iridiumTeams.getTeamManager().isInTeam(team, stack.getEntity().getLocation())) continue;
        //     if (material != XMaterial.matchXMaterial(stack.getEntity().getLocation().getBlock().getType())) continue;
        //     stackedBlocks += (stack.getCount() - 1);
        // }
        return stackedBlocks; // Return 0
    }
}
