package ru.melonhell.bulletphysics.bullet.collision.body

import com.jme3.bullet.collision.shapes.CollisionShape
import com.jme3.bullet.objects.PhysicsRigidBody
import com.jme3.math.Vector3f
import org.bukkit.block.BlockState
import ru.melonhell.bulletphysics.bullet.collision.body.shape.MinecraftShape
import ru.melonhell.bulletphysics.bullet.collision.space.block.BlockProperty.Companion.getBlockProperty
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data.PhysicsBlockData
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos

class TerrainRigidBody(
    shape: MinecraftShape?,
    val blockPos: BlockPos,
    val blockState: BlockState,
    friction: Float,
    restitution: Float
) : PhysicsRigidBody(shape as CollisionShape?, massForStatic) {

    init {
        this.friction = friction
        this.restitution = restitution
        this.setPhysicsLocation(Vector3f(blockPos.x.toFloat(), blockPos.y.toFloat(), blockPos.z.toFloat()))
    }

    override fun equals(other: Any?): Boolean {
        if (other is TerrainRigidBody) {
            return other.blockPos == this.blockPos && other.blockState == this.blockState
        }
        return false
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + blockPos.hashCode()
        result = 31 * result + blockState.hashCode()
        return result
    }

    companion object {
        fun from(physicsBlockData: PhysicsBlockData): TerrainRigidBody {
            val blockProperty = getBlockProperty(physicsBlockData.blockState.type)
            val friction = blockProperty?.friction ?: 0.75f
            val restitution = blockProperty?.restitution ?: 0.25f
            return TerrainRigidBody(
                physicsBlockData.shape,
                BlockPos(physicsBlockData.block),
                physicsBlockData.blockState,
                friction,
                restitution
            )
        }
    }
}