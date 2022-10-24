package ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.Nullable;
import ru.melonhell.bulletphysics.bullet.collision.body.shape.MinecraftShape;

public record PhysicsBlockData(Block block, BlockState blockState, @Nullable MinecraftShape shape) {
}
