package com.pluncky.naturalspawners.listener;

import com.pluncky.bukkitutils.utils.messages.ErrorMessages;
import com.pluncky.naturalspawners.NaturalSpawnersPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@RequiredArgsConstructor
public class SpawnerPlaceListener implements Listener {
    private final NaturalSpawnersPlugin plugin;

    @EventHandler
    private void onSpawnerPlace(BlockPlaceEvent event) {
        if (!(event.getBlockPlaced().getState() instanceof CreatureSpawner creatureSpawner)) {
            return;
        }

        final Player player = event.getPlayer();
        final ItemStack spawnerItem = event.getItemInHand().clone();

        ItemMeta meta = spawnerItem.getItemMeta();
        if (meta == null) {
            return;
        };

        final PersistentDataContainer container = meta.getPersistentDataContainer();
        final NamespacedKey key = new NamespacedKey(plugin, "spawner_entity");

        final String entityName = container.get(key, PersistentDataType.STRING);

        if (entityName == null) {
            player.sendMessage(ErrorMessages.INTERNAL_ERROR.getMessage());
            event.setCancelled(true);
            return;
        }

        creatureSpawner.setSpawnedType(EntityType.valueOf(entityName));
        creatureSpawner.update();
    }
}
