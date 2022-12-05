package ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid

import ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.data.FluidColumn
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos

interface FluidCache {
    fun refreshFluidData(blockPos: BlockPos)
    fun getFluidColumn(blockPos: BlockPos): FluidColumn?
}