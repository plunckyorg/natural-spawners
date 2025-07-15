package com.pluncky.naturalspawners.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class EntityTypeArgument implements CustomArgumentType.Converted<EntityType, String> {
    private static final EntityTypeArgument INSTANCE = new EntityTypeArgument();

    private static final DynamicCommandExceptionType ERROR_INVALID_ENTITY = new DynamicCommandExceptionType(
            name -> MessageComponentSerializer.message().serialize(Component.text("Tipo de spawner inválido.", NamedTextColor.RED))
    );

    private EntityTypeArgument() {
    }

    public static EntityTypeArgument get() {
        return INSTANCE;
    }

    @Override
    public @NotNull EntityType convert(String entityName) throws CommandSyntaxException {
        try {
            EntityType entityType = EntityType.valueOf(entityName.toUpperCase());
            if (!entityType.isSpawnable() || !entityType.isAlive()) {
                throw ERROR_INVALID_ENTITY.create(entityName);
            }
            return entityType;
        } catch (IllegalArgumentException e) {
            throw ERROR_INVALID_ENTITY.create(entityName);
        }
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        final String input = builder.getRemaining().toUpperCase();

        for (EntityType entityType : EntityType.values()) {
            if (!entityType.isAlive() || !entityType.isSpawnable()) {
                continue;
            }

            if (entityType.name().startsWith(input)) {
                builder.suggest(entityType.name());
            }
        }

        return builder.buildFuture();
    }
}