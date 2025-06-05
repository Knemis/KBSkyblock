package com.kbskyblock.teams.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.PermissionType;
import com.kbskyblock.teams.SettingType;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.teams.database.TeamSetting;
import com.kbskyblock.teams.database.TeamSpawners;
import com.kbskyblock.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;

import java.util.Arrays;
import java.util.List;

    @AllArgsConstructor
    public class PlayerInteractListener<T extends Team, U extends KBSkyblockUser<T>> implements Listener {
        private final KBSkyblockTeams<T, U> teams;
        private final List<XMaterial> redstoneTriggers = Arrays.asList(XMaterial.LEVER, XMaterial.STRING, XMaterial.TRIPWIRE, XMaterial.TRIPWIRE_HOOK, XMaterial.SCULK_SENSOR, XMaterial.CALIBRATED_SCULK_SENSOR);

        @EventHandler(ignoreCancelled = true)
        public void onPlayerInteract(PlayerInteractEvent event) {
            if (event.getClickedBlock() == null) return;
            Player player = event.getPlayer();
            U user = teams.getUserManager().getUser(player);

            teams.getTeamManager().getTeamViaPlayerLocation(player, event.getClickedBlock().getLocation()).ifPresent(team -> {
                if (!teams.getTeamManager().getTeamPermission(team, user, PermissionType.OPEN_CONTAINERS.getPermissionKey()) && event.getClickedBlock().getState() instanceof InventoryHolder) {
                    player.sendMessage(StringUtils.color(teams.getMessages().cannotOpenContainers
                            .replace("%prefix%", teams.getConfiguration().prefix)
                    ));
                    event.setCancelled(true);
                }

                if (!teams.getTeamManager().getTeamPermission(team, user, PermissionType.DOORS.getPermissionKey()) && isDoor(XMaterial.matchXMaterial(event.getClickedBlock().getType()))) {
                    player.sendMessage(StringUtils.color(teams.getMessages().cannotOpenDoors
                            .replace("%prefix%", teams.getConfiguration().prefix)
                    ));
                    event.setCancelled(true);
                }

                if (!teams.getTeamManager().getTeamPermission(team, user, PermissionType.REDSTONE.getPermissionKey()) && isRedstoneTrigger(XMaterial.matchXMaterial(event.getClickedBlock().getType()))) {
                    player.sendMessage(StringUtils.color(teams.getMessages().cannotTriggerRedstone
                            .replace("%prefix%", teams.getConfiguration().prefix)
                    ));
                    event.setCancelled(true);
                }

                if (event.getAction() == Action.PHYSICAL) {
                    TeamSetting cropTrampleTeamSetting = teams.getTeamManager().getTeamSetting(team, SettingType.CROP_TRAMPLE.getSettingKey());
                    if (cropTrampleTeamSetting == null) return;
                    if (cropTrampleTeamSetting.getValue().equalsIgnoreCase("Disabled") && (XMaterial.matchXMaterial(event.getClickedBlock().getType()) == XMaterial.FARMLAND)) {
                        event.setCancelled(true);
                    }
                }

                if(isSpawner(XMaterial.matchXMaterial(event.getClickedBlock().getType()))
                        && (isSpawnEgg(event.getPlayer().getInventory().getItemInMainHand().getItemMeta())
                        || isSpawnEgg(event.getPlayer().getInventory().getItemInOffHand().getItemMeta()))) {

                    if (!teams.getTeamManager().getTeamPermission(team, user, PermissionType.SPAWNERS.getPermissionKey())) {
                        player.sendMessage(StringUtils.color(teams.getMessages().cannotBreakSpawners
                                .replace("%prefix%", teams.getConfiguration().prefix)
                        ));
                        event.setCancelled(true);
                    } else {

                        ItemStack itemStack;
                        if(isSpawnEgg(event.getPlayer().getInventory().getItemInMainHand().getItemMeta())) itemStack = event.getPlayer().getInventory().getItemInMainHand();
                        else itemStack = event.getPlayer().getInventory().getItemInOffHand();

                        CreatureSpawner creatureSpawner = (CreatureSpawner) event.getClickedBlock().getState();

                        EntityType newEntityType = getEntityType(itemStack);
                        if(newEntityType == EntityType.UNKNOWN) newEntityType = creatureSpawner.getSpawnedType();

                        TeamSpawners teamSpawners;
                        if(creatureSpawner.getSpawnedType() != null) {
                            teamSpawners = teams.getTeamManager().getTeamSpawners(team, creatureSpawner.getSpawnedType());
                            teamSpawners.setAmount(Math.max(0, teamSpawners.getAmount() - 1));
                        }

                        teamSpawners = teams.getTeamManager().getTeamSpawners(team, newEntityType);
                        teamSpawners.setAmount(teamSpawners.getAmount() + 1);
                    }
                }
            });
        }

        @EventHandler
        public void onSignChangeEvent(SignChangeEvent event) {
            Player player = event.getPlayer();
            U user = teams.getUserManager().getUser(player);

            teams.getTeamManager().getTeamViaPlayerLocation(player, event.getBlock().getLocation()).ifPresent(team -> {
                if (!teams.getTeamManager().getTeamPermission(team, user, PermissionType.INTERACT.getPermissionKey())) {
                    player.sendMessage(StringUtils.color(teams.getMessages().cannotInteract
                            .replace("%prefix%", teams.getConfiguration().prefix)));
                    event.setCancelled(true);
                }
            });
        }

        @EventHandler
        public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
            Player player = event.getPlayer();
            U user = teams.getUserManager().getUser(player);

            teams.getTeamManager().getTeamViaPlayerLocation(player, event.getRightClicked().getLocation()).ifPresent(team -> {
                if (!teams.getTeamManager().getTeamPermission(team, user, PermissionType.INTERACT.getPermissionKey())) {
                    player.sendMessage(StringUtils.color(teams.getMessages().cannotInteract
                            .replace("%prefix%", teams.getConfiguration().prefix)));
                    event.setCancelled(true);
                }
            });
        }

        private EntityType getEntityType(ItemStack itemStack) {
            try {
                return EntityType.valueOf(itemStack.getType().name().toUpperCase().replace("_SPAWN_EGG", ""));
            }
            catch(NullPointerException e) {
                teams.getLogger().warning(e.getMessage());
                return EntityType.UNKNOWN;
            }
        }

        private boolean isSpawner(XMaterial material) {
            return material.name().toLowerCase().contains("spawner");
        }

        private boolean isSpawnEgg(ItemMeta itemMeta) {
            return itemMeta instanceof SpawnEggMeta;
        }

        private boolean isRedstoneTrigger(XMaterial material) {
            return redstoneTriggers.contains(material) || material.name().toLowerCase().contains("_button") || material.name().toLowerCase().contains("_pressure_plate");
        }

        private boolean isDoor(XMaterial material) {
            return material.name().toLowerCase().contains("_door") || material.name().toLowerCase().contains("fence_gate") || material.name().toLowerCase().contains("trapdoor");
        }
    }
