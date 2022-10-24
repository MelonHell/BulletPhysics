package ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.impl;

import ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.FluidCache;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.data.FluidColumn;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos;

public class EmptyFluidCache implements FluidCache {
    @Override
    public void refreshFluidData(BlockPos blockPos) {

    }

    @Override
    public FluidColumn getFluidColumn(BlockPos blockPos) {
        return null;
    }
}
