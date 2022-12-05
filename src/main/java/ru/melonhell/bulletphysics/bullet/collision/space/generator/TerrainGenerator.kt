package ru.melonhell.bulletphysics.bullet.collision.space.generator

import com.jme3.bounding.BoundingBox
import org.springframework.stereotype.Component
import ru.melonhell.bulletphysics.bullet.collision.body.TerrainRigidBody
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace
import ru.melonhell.bulletphysics.nms.NmsTools
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos
import ru.melonhell.bulletphysics.utils.math.BetweenClosedUtils.betweenClosed
import ru.melonhell.bulletphysics.utils.math.BoundingBoxUtils.inflate

/**
 * Used for loading blocks into the simulation so that rigid bodies can interact with them.
 *
 * @see MinecraftSpace
 */
@Component
class TerrainGenerator(private val nmsTools: NmsTools) {
    fun step(space: MinecraftSpace) {
        val blockCache = space.blockCache
        val keep = HashSet<TerrainRigidBody?>()
        val betweenClosed: MutableList<BlockPos> = ArrayList()
        for (elementRigidBodyData in space.physicsElements) {
            val rigidBody = elementRigidBodyData.rigidBody
            if (!elementRigidBodyData.isTerrainLoadingEnabled || !rigidBody.isActive) continue
            val box = rigidBody.boundingBox(BoundingBox())
            inflate(box, 3.0f)
            betweenClosed(box, betweenClosed)
        }
        for (blockPos in betweenClosed) {
            val physicsBlockData = blockCache.getBlockData(blockPos)
            if (physicsBlockData.shape != null) {
                var terrain = space.getTerrainObjectAt(blockPos)
                if (terrain != null) {
                    if (!nmsTools.equalsById(physicsBlockData.blockState, terrain.blockState)) {
                        space.removeCollisionObject(terrain)
                        terrain = TerrainRigidBody.from(physicsBlockData)
                        space.addCollisionObject(terrain)
                    }
                } else {
                    terrain = TerrainRigidBody.from(physicsBlockData)
                    space.addCollisionObject(terrain)
                }
                keep.add(terrain)
            }
        }
        space.getTerrainMap().forEach { (blockPos: BlockPos?, terrain: TerrainRigidBody?) ->
            if (!keep.contains(terrain)) {
                space.removeTerrainObjectAt(blockPos)
            }
        }
    }
}