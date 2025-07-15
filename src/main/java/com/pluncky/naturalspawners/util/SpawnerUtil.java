package com.pluncky.naturalspawners.util;

import com.pluncky.naturalspawners.NaturalSpawnersPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SpawnerUtil {
    public static ItemStack buildSpawner(NaturalSpawnersPlugin plugin, EntityType entityType) {
        final ItemStack spawnerItem = new ItemStack(Material.SPAWNER);
        final ItemMeta meta = spawnerItem.getItemMeta();

        if (meta == null) {
            return null;
        }

        final Component creatureName = Component.translatable(entityType.translationKey()).color(NamedTextColor.WHITE);
        final Component spawnerName =
                Component.text("Spawner de ", NamedTextColor.WHITE)
                        .append(creatureName)
                        .decoration(TextDecoration.ITALIC, false);

        meta.displayName(spawnerName);

        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

        final PersistentDataContainer container = meta.getPersistentDataContainer();
        final NamespacedKey key = new NamespacedKey(plugin, "spawner_entity");

        container.set(key, PersistentDataType.STRING, entityType.name());

        spawnerItem.setItemMeta(meta);

        return spawnerItem;
    }
}
