package com.pluncky.naturalspawners.listener;

import com.pluncky.bukkitutils.utils.messages.ErrorMessages;
import com.pluncky.naturalspawners.NaturalSpawnersPlugin;
import com.pluncky.naturalspawners.util.SpawnerUtil;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Set;

@RequiredArgsConstructor
public class SpawnerBreakListener implements Listener {
    private static final Set<Material> PICKAXES = Set.of(
            Material.WOODEN_PICKAXE,
            Material.STONE_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.NETHERITE_PICKAXE
    );

    private final NaturalSpawnersPlugin plugin;

    @EventHandler
    private void onSpawnerBreak(BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof CreatureSpawner spawner)) {
            return;
        }

        final Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        final ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (!PICKAXES.contains(itemInHand.getType()) && !player.hasPermission("naturalspawners.admin.break")) {
            event.setCancelled(true);
            player.sendMessage("§cVocê precisa de uma picareta para quebrar spawners.");
            return;
        }

        final EntityType entityType = spawner.getSpawnedType();
        if (entityType == null) {
            player.sendMessage(ErrorMessages.INTERNAL_ERROR.getMessage());
            event.setCancelled(true);
            return;
        }

        final ItemStack spawnerItem = SpawnerUtil.buildSpawner(this.plugin, entityType);

        if (spawnerItem == null) {
            player.sendMessage(ErrorMessages.INTERNAL_ERROR.getMessage());
            event.setCancelled(true);
            return;
        }

        event.setExpToDrop(0);

        HashMap<Integer, ItemStack> remaining = player.getInventory().addItem(spawnerItem);
        for (ItemStack item : remaining.values()) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
        }
    }
}
