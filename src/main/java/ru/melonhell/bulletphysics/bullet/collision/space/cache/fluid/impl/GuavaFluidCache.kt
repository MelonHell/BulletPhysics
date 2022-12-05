package ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.impl

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace
import ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.FluidCache
import ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.data.FluidColumn
import ru.melonhell.bulletphysics.nms.NmsTools
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos
import java.util.*
import java.util.concurrent.TimeUnit

class GuavaFluidCache(private val space: MinecraftSpace, private val nmsTools: NmsTools) : FluidCache {
    private val fluidDataCache = CacheBuilder.newBuilder()
        .expireAfterAccess(10, TimeUnit.SECONDS)
        .build<BlockPos, Optional<FluidColumn>>(CacheLoader.from { blockPos -> loadFluidData(blockPos) })

    override fun refreshFluidData(blockPos: BlockPos) {
        fluidDataCache.invalidate(blockPos)
    }

    private fun loadFluidData(blockPos: BlockPos): Optional<FluidColumn> {
        val world = space.world
        return if (!nmsTools.getFluidState(blockPos.getBlock(world)).isEmpty) {
            Optional.of(FluidColumn(blockPos.getBlock(world), nmsTools))
        } else Optional.empty()
    }

    override fun getFluidColumn(blockPos: BlockPos): FluidColumn? {
        return fluidDataCache[blockPos].orElse(null)
    }
}