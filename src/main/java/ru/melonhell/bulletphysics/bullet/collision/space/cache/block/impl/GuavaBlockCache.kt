package ru.melonhell.bulletphysics.bullet.collision.space.cache.block.impl

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import org.bukkit.block.BlockState
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.BlockCache
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data.BlockDataUtils
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data.PhysicsBlockData
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos
import java.util.concurrent.TimeUnit

class GuavaBlockCache(
    override val space: MinecraftSpace,
    private val blockDataUtils: BlockDataUtils
) : BlockCache {
    private val physicsBlockDataCache: LoadingCache<BlockPos, PhysicsBlockData>

    init {
        physicsBlockDataCache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.SECONDS)
            .build(CacheLoader.from { blockPos: BlockPos -> loadBlockData(blockPos) })
    }

    override fun refreshBlockData(blockPos: BlockPos, blockState: BlockState) {
        physicsBlockDataCache.invalidate(blockPos)
    }

    private fun loadBlockData(blockPos: BlockPos): PhysicsBlockData {
        return blockDataUtils.getPhysicsBlockData(blockPos.getBlock(space.world))
    }

    override fun getBlockData(blockPos: BlockPos): PhysicsBlockData {
        return physicsBlockDataCache[blockPos]
    }
}