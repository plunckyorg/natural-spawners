package com.pluncky.naturalspawners;

import com.pluncky.naturalspawners.command.SpawnerCommand;
import com.pluncky.naturalspawners.listener.SpawnerBreakListener;
import com.pluncky.naturalspawners.listener.SpawnerPlaceListener;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NaturalSpawnersPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        this.init();
        getLogger().info("Natural Spawners plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Natural Spawners plugin disabled!");
    }

    private void init() {
        saveDefaultConfig();
        this.registerListeners(
                new SpawnerBreakListener(this),
                new SpawnerPlaceListener(this)
        );
        this.registerCommands();
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pluginManager = getServer().getPluginManager();

        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, this);
        }
    }

    private void registerCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(new SpawnerCommand(this).buildCommand());
        });
    }
}
