package ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid;

import org.jetbrains.annotations.Nullable;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.data.FluidColumn;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos;

public interface FluidCache {
    void refreshFluidData(BlockPos blockPos);

    @Nullable
    FluidColumn getFluidColumn(BlockPos blockPos);
}
