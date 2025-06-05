
package com.kbskyblock.teams.listeners;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

@AllArgsConstructor
public class PotionBrewListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
    private final KBSkyblockTeams<T, U> teams;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void monitorPotionBrew(BrewEvent event) {
        Bukkit.getScheduler().runTask(teams, () -> teams.getTeamManager().getTeamViaLocation(event.getBlock().getLocation()).ifPresent(team -> {
            for (int i = 0; i < 3; i++) {
                ItemStack itemStack = event.getContents().getItem(i);
                if (itemStack != null && itemStack.getItemMeta() instanceof PotionMeta) {
                    PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
                    teams.getMissionManager().handleMissionUpdate(team, event.getBlock().getLocation().getWorld(), "BREW", potionMeta.getBasePotionData().getType() + ":" + (potionMeta.getBasePotionData().isUpgraded() ? 2 : 1), 1);
                }
            }
        }));
    }

}
