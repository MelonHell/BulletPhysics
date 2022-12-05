package ru.melonhell.bulletphysics.nms.wrappers

import com.jme3.bounding.BoundingBox
import com.jme3.math.Vector3f
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockState

data class BlockPos(
    val x: Int,
    val y: Int,
    val z: Int
) {
    constructor(block: Block): this(block.x, block.y, block.z)

    fun getBlock(world: World): Block {
        return world.getBlockAt(x, y, z)
    }

    fun getBlockState(world: World): BlockState {
        return world.getBlockState(x, y, z)
    }

    fun boundingBox(): BoundingBox {
        return BoundingBox(
            Vector3f(x.toFloat(), y.toFloat(), z.toFloat()),
            Vector3f((x + 1).toFloat(), (y + 1).toFloat(), (z + 1).toFloat())
        )
    }
}