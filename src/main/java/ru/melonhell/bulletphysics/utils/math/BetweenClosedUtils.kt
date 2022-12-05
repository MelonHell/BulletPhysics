package ru.melonhell.bulletphysics.utils.math

import com.jme3.bounding.BoundingBox
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos

object BetweenClosedUtils {
    fun betweenClosed(box: BoundingBox): List<BlockPos> {
        val result: MutableList<BlockPos> = ArrayList()
        betweenClosed(box, result)
        return result
    }

    fun betweenClosed(box: BoundingBox, result: MutableCollection<BlockPos>) {
        val min = box.getMin(null)
        val max = box.getMax(null)
        betweenClosed(
            Math.floor(min.x.toDouble()).toInt(),
            Math.floor(min.y.toDouble()).toInt(),
            Math.floor(min.z.toDouble()).toInt(),
            Math.floor(max.x.toDouble()).toInt(),
            Math.floor(max.y.toDouble()).toInt(),
            Math.floor(max.z.toDouble()).toInt(),
            result
        )
    }

    fun betweenClosed(startX: Int, startY: Int, startZ: Int, endX: Int, endY: Int, endZ: Int): List<BlockPos> {
        val result: MutableList<BlockPos> = ArrayList()
        betweenClosed(startX, startY, startZ, endX, endY, endZ, result)
        return result
    }

    fun betweenClosed(
        startX: Int,
        startY: Int,
        startZ: Int,
        endX: Int,
        endY: Int,
        endZ: Int,
        result: MutableCollection<BlockPos>
    ) {
        for (x in startX until endX) {
            for (y in startY until endY) {
                for (z in startZ until endZ) {
                    result.add(BlockPos(x, y, z))
                }
            }
        }
    }
}