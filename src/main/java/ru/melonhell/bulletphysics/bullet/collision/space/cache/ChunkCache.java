package ru.melonhell.bulletphysics.bullet.collision.space.cache;

import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.data.BlockData;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.data.FluidColumn;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos;

import java.util.List;
import java.util.Optional;

/**
 * Used for storing block that can be queried during physics execution.
 * An implementation of this should be updated/reloaded every tick on the
 * main game thread.
 *
 * @see MinecraftSpace#step
 */
public interface ChunkCache {
    void refreshAll();

    void loadBlockData(BlockPos blockPos);

    void loadFluidData(BlockPos blockPos);

    MinecraftSpace getSpace();

    List<BlockData> getBlockData();

    List<FluidColumn> getFluidColumns();

    Optional<BlockData> getBlockData(BlockPos blockPos);

    Optional<FluidColumn> getFluidColumn(BlockPos blockPos);
}