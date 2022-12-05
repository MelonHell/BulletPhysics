package ru.melonhell.bulletphysics.bullet.collision.space.cache.block.impl

import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.BlockCache
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data.BlockDataUtils
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data.PhysicsBlockData
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos

class DirectBlockCache(
    override val space: MinecraftSpace,
    private val blockDataUtils: BlockDataUtils
) : BlockCache {
    override fun getBlockData(blockPos: BlockPos): PhysicsBlockData {
        return blockDataUtils.getPhysicsBlockData(blockPos.getBlock(space.world))
    }
}