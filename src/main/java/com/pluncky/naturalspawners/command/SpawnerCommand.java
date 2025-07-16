package com.pluncky.naturalspawners.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.pluncky.bukkitutils.utils.inventory.InventoryUtils;
import com.pluncky.bukkitutils.utils.messages.ErrorMessages;
import com.pluncky.naturalspawners.NaturalSpawnersPlugin;
import com.pluncky.naturalspawners.command.argument.EntityTypeArgument;
import com.pluncky.naturalspawners.util.SpawnerUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class SpawnerCommand {
    private final NaturalSpawnersPlugin plugin;

    public LiteralCommandNode<CommandSourceStack> buildCommand() {
        return Commands.literal("spawner")
                .requires(source -> source.getSender().hasPermission("naturalspawners.admin.give"))
                .then(Commands.literal("dar")
                        .then(Commands.argument("jogador", ArgumentTypes.player())
                                // Remove selectors from suggestions but they are still valid inputs
                                .suggests((context, builder) -> {
                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        if (player.getName().startsWith(builder.getRemaining())) {
                                            builder.suggest(player.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("tipo", EntityTypeArgument.get())
                                        .executes(this::execute)
                                        .then(Commands.argument("quantidade", IntegerArgumentType.integer(1, 2304))
                                                .executes(this::execute)
                                        )
                                )
                        )
                ).build();
    }

    private int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final CommandSender sender = context.getSource().getSender();

        final Player target = context.getArgument("jogador", PlayerSelectorArgumentResolver.class)
                .resolve(context.getSource()).stream().findFirst().orElse(null);

        final EntityType entityType = context.getArgument("tipo", EntityType.class);

        int amount;
        try {
            amount = IntegerArgumentType.getInteger(context, "quantidade");
        } catch (IllegalArgumentException e) {
            amount = 1;
        }

        if (target == null) {
            sender.sendMessage(Component.text(ErrorMessages.PLAYER_NOT_FOUND.getMessage(), NamedTextColor.RED));
            return 0;
        }

        if (!entityType.isSpawnable() || !entityType.isAlive()) {
            sender.sendMessage(Component.text("Tipo de spawner inválido.", NamedTextColor.RED));
            return 0;
        }

        final ItemStack spawner = SpawnerUtil.buildSpawner(this.plugin, entityType);
        if (spawner == null) {
            sender.sendMessage(Component.text(ErrorMessages.INTERNAL_ERROR.getMessage(), NamedTextColor.RED));
            return 0;
        }

        spawner.setAmount(amount);

        if (InventoryUtils.getMissingAmount(target.getInventory(), spawner) < amount) {
            sender.sendMessage(Component.text(ErrorMessages.INVENTORY_FULL.getMessage(), NamedTextColor.RED));
            return 0;
        }

        target.getInventory().addItem(spawner);

        final Component creatureName = Component.translatable(entityType.translationKey()).color(NamedTextColor.WHITE);
        final Component message = Component.text("Spawner de ", NamedTextColor.GREEN)
                .append(creatureName)
                .append(Component.text(" (x" + amount + ")", NamedTextColor.WHITE))
                .append(Component.text(" dado com sucesso para ", NamedTextColor.GREEN))
                .append(Component.text(target.getName(), NamedTextColor.WHITE))
                .append(Component.text(".", NamedTextColor.GREEN));

        sender.sendMessage(message);

        return 1;
    }
}