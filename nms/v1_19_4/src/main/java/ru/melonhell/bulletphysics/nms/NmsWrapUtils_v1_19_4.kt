package ru.melonhell.bulletphysics.nms

import com.jme3.bounding.BoundingBox
import com.jme3.math.Vector3f
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.phys.AABB
import ru.melonhell.bulletphysics.nms.wrappers.FluidStateWrapper


object NmsWrapUtils_v1_19_4 {
    fun convert(aabb: AABB): BoundingBox {
        return BoundingBox(
            Vector3f(aabb.minX.toFloat(), aabb.minY.toFloat(), aabb.minZ.toFloat()),
            Vector3f(aabb.maxX.toFloat(), aabb.maxY.toFloat(), aabb.maxZ.toFloat())
        )
    }

    fun unwrap(fluidState: FluidStateWrapper): FluidState {
        return fluidState.handle as FluidState
    }

    fun wrap(fluidState: FluidState): FluidStateWrapper {
        return FluidStateWrapper(fluidState, fluidState.isEmpty)
    }
}