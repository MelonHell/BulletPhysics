package ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.data

import com.jme3.math.Vector3f
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import ru.melonhell.bulletphysics.nms.NmsTools
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos
import ru.melonhell.bulletphysics.utils.math.Convert.toBullet

class FluidColumn(start: Block, nmsTools: NmsTools) {
    val top: FluidData
    val bottom: FluidData
    val flow: Vector3f
    private val height: Float

    init {
        var cursor = start
        var fluidState = nmsTools.getFluidState(cursor)

        // find bottom block
        while (!fluidState.isEmpty) {
            cursor = cursor.getRelative(BlockFace.DOWN)
            fluidState = nmsTools.getFluidState(cursor)
        }
        cursor = cursor.getRelative(BlockFace.UP) // the above loop ends at one below the bottom
        fluidState = nmsTools.getFluidState(cursor)
        bottom = FluidData(cursor, nmsTools.getFluidState(cursor))

        // find top block
        while (!fluidState.isEmpty) {
            cursor = cursor.getRelative(BlockFace.UP)
            fluidState = nmsTools.getFluidState(cursor)
        }
        cursor = cursor.getRelative(BlockFace.DOWN)
        fluidState = nmsTools.getFluidState(cursor)
        top = FluidData(cursor, fluidState)
        height = nmsTools.getHeight(fluidState, cursor)

        // Water flow direction
        flow = toBullet(nmsTools.getFlow(fluidState, cursor))
    }

    operator fun contains(blockPos: BlockPos): Boolean {
        return top.block.x == blockPos.x && top.block.z == blockPos.z && top.block.y >= blockPos.y && bottom.block.y <= blockPos.y
    }

    fun getTopHeight(position: Vector3f?): Float {
//            if (flow.lengthSquared() == 0) {
//                return 0.875f;
//            }
//            final var x = position.x;
//            final var z = position.z;
//
//            final var minHeight = 0.125f;
//            final var maxHeight = height;
//
//            final var xhat = flow.dot(new Vector3f(1, 0, 0));
//            final var zhat = flow.dot(new Vector3f(0, 0, 1));
//
//            if (xhat == 0) {
//                if (zhat > 0) {
//                    return (1.0f - z) * maxHeight + z * minHeight;
//                } else if (zhat < 0) {
//                    return (1.0f - z) * minHeight + z * maxHeight;
//                }
//            } else if (zhat == 0) {
//                if (xhat > 0) {
//                    return (1.0f - x) * maxHeight + x * minHeight;
//                } else if (xhat < 0) {
//                    return (1.0f - x) * minHeight + x * maxHeight;
//                }
//            }

//            final var hitResult = topShape.clip(VectorHelper.toVec3(Convert.toMinecraft(position)), new Vec3(top.blockPos.getX() + 0.5f, top.blockPos.getY(), top.blockPos.getZ() + 0.5f), top.blockPos);
//            final var y = topShape.collide(Direction.Axis.Y, new AABB(top.blockPos).move(VectorHelper.toVec3(Convert.toMinecraft(position))), 0.875f);

//            if (hitResult != null) {
//                return position.y - (float) hitResult.getLocation().y;
//            }
        return height
    }

    fun getHeight(): Int {
        return top.block.y - bottom.block.y + 1
    }
}