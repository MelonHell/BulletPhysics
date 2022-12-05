package ru.melonhell.bulletphysics.bullet.collision.space.cache.block

import org.bukkit.block.BlockState
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data.PhysicsBlockData
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos

/**
 * Used for storing block that can be queried during physics execution.
 * An implementation of this should be updated/reloaded every tick on the
 * main game thread.
 *
 * @see MinecraftSpace.step
 */
interface BlockCache {
    val space: MinecraftSpace
    fun refreshBlockData(blockPos: BlockPos, blockState: BlockState) {}
    fun getBlockData(blockPos: BlockPos): PhysicsBlockData
}