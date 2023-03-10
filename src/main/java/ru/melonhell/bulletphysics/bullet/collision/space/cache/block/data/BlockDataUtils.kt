package ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data

import com.jme3.bounding.BoundingBox
import com.jme3.math.Vector3f
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.springframework.stereotype.Component
import ru.melonhell.bulletphysics.bullet.collision.body.shape.MinecraftShape
import ru.melonhell.bulletphysics.bullet.collision.body.shape.impl.ConvexMinecraftShape
import ru.melonhell.bulletphysics.bullet.collision.space.block.BlockProperty
import ru.melonhell.bulletphysics.nms.NmsTools

@Component
class BlockDataUtils(private val nmsTools: NmsTools) {
    fun getPhysicsBlockData(block: Block): PhysicsBlockData {
        return getPhysicsBlockData(block, nmsTools.getBlockState(block))
    }

    fun getPhysicsBlockData(block: Block, blockState: BlockState): PhysicsBlockData {
        var shape: MinecraftShape? = null
        if (blockState.type == Material.STRUCTURE_VOID) {
            shape = ConvexMinecraftShape.of(BoundingBox(Vector3f(0.5f, 0.5f, 0.5f), 0.5f, 0.5f, 0.5f))
        } else if (isValidBlock(blockState)) {
            val boundingBoxes = nmsTools.boundingBoxes(block, blockState)
            if (boundingBoxes.isNotEmpty()) {
                shape = ConvexMinecraftShape.of(boundingBoxes)
            }
        }
        return PhysicsBlockData(block, blockState, shape)
    }

    fun isValidBlock(blockState: BlockState?): Boolean {
        if (blockState == null) return false
        val block = blockState.type
        val properties = BlockProperty.getBlockProperty(block)
        return properties?.collidable ?: nmsTools.collidableCheck(blockState)
    }
}