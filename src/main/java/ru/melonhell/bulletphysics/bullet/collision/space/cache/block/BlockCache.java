package ru.melonhell.bulletphysics.bullet.collision.space.cache.block;

import org.bukkit.block.BlockState;
import org.jetbrains.annotations.Nullable;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data.PhysicsBlockData;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos;

/**
 * Used for storing block that can be queried during physics execution.
 * An implementation of this should be updated/reloaded every tick on the
 * main game thread.
 *
 * @see MinecraftSpace#step
 */
public interface BlockCache {
    MinecraftSpace getSpace();

    default void refreshBlockData(BlockPos blockPos, BlockState blockState) {}

    @Nullable
    PhysicsBlockData getBlockData(BlockPos blockPos);
}