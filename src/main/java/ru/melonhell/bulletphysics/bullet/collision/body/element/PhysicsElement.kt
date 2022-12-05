package ru.melonhell.bulletphysics.bullet.collision.body.element

import com.jme3.bounding.BoundingBox
import com.jme3.bullet.objects.PhysicsRigidBody
import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import ru.melonhell.bulletphysics.bullet.collision.body.shape.MinecraftShape
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos
import ru.melonhell.bulletphysics.utils.math.BoundingBoxUtils.inflate
import ru.melonhell.bulletphysics.utils.math.BoundingBoxUtils.intersects

open class PhysicsElement(
    val rigidBody: PhysicsRigidBody
) {
    val frame = Frame()
    val sleepTimer = Clock()
    var isTerrainLoadingEnabled: Boolean
    var dragCoefficient = 0.25f
    var buoyancyType = BuoyancyType.WATER
    var dragType = DragType.SIMPLE

    init {
        isTerrainLoadingEnabled = !rigidBody.isStatic
    }

    open fun updateFrame() {
        frame.from(frame, rigidBody.getPhysicsLocation(Vector3f()), rigidBody.getPhysicsRotation(Quaternion()))
    }

    fun isNear(blockPos: BlockPos): Boolean {
        val boundingBox1 = rigidBody.boundingBox(BoundingBox())
        val boundingBox2 = blockPos.boundingBox()
        inflate(boundingBox2, 0.5f)
        return intersects(boundingBox1, boundingBox2)
    }

    val isWaterBuoyancyEnabled: Boolean
        get() = buoyancyType == BuoyancyType.WATER || buoyancyType == BuoyancyType.ALL
    val isAirBuoyancyEnabled: Boolean
        get() = buoyancyType == BuoyancyType.AIR || buoyancyType == BuoyancyType.ALL

    // We check for simple drag here, but complex drag is always used for water buoyancy.
    val isWaterDragEnabled: Boolean
        get() =// We check for simple drag here, but complex drag is always used for water buoyancy.
            dragType == DragType.WATER || dragType == DragType.ALL || dragType == DragType.SIMPLE
    val isAirDragEnabled: Boolean
        get() = dragType == DragType.AIR || dragType == DragType.ALL
    val minecraftShape: MinecraftShape
        get() = rigidBody.collisionShape as MinecraftShape

    enum class BuoyancyType {
        NONE, AIR, WATER, ALL
    }

    enum class DragType {
        NONE, AIR, WATER, SIMPLE, ALL
    }

    companion object {
        const val SLEEP_TIME_IN_SECONDS = 2.0f
    }
}