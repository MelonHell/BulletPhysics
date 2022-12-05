package ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.impl

import ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.FluidCache
import ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.data.FluidColumn
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos

class EmptyFluidCache : FluidCache {
    override fun refreshFluidData(blockPos: BlockPos) = Unit
    override fun getFluidColumn(blockPos: BlockPos): FluidColumn? = null
}