package ru.melonhell.bulletphysics.bullet.collision.space.cache;

import org.bukkit.block.BlockState;
import org.jetbrains.annotations.Nullable;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.data.FluidColumn;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.data.PhysicsBlockData;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos;

import java.util.List;

/**
 * Used for storing block that can be queried during physics execution.
 * An implementation of this should be updated/reloaded every tick on the
 * main game thread.
 *
 * @see MinecraftSpace#step
 */
public interface BlockCache {
    void refreshAll();

    void loadBlockData(BlockPos blockPos, BlockState blockState);

    void loadFluidData(BlockPos blockPos);

    MinecraftSpace getSpace();

    List<PhysicsBlockData> getPhysicsBlockDataMap();

    List<FluidColumn> getFluidColumns();

    @Nullable
    PhysicsBlockData getBlockData(BlockPos blockPos);

    @Nullable
    FluidColumn getFluidColumn(BlockPos blockPos);
}