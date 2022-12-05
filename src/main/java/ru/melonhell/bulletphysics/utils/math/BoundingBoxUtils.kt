package ru.melonhell.bulletphysics.utils.math

import com.jme3.bounding.BoundingBox

object BoundingBoxUtils {
    @JvmStatic
    fun inflate(boundingBox: BoundingBox, value: Float) {
        inflate(boundingBox, value, value, value)
    }


    @JvmStatic
    fun inflate(boundingBox: BoundingBox, x: Float, y: Float, z: Float) {
        boundingBox.xExtent = boundingBox.xExtent + x
        boundingBox.yExtent = boundingBox.yExtent + y
        boundingBox.zExtent = boundingBox.zExtent + z
    }

    @JvmStatic
    fun intersects(boundingBox1: BoundingBox, boundingBox2: BoundingBox): Boolean {
        val min1 = boundingBox1.getMin(null)
        val max1 = boundingBox1.getMax(null)
        val min2 = boundingBox2.getMin(null)
        val max2 = boundingBox2.getMax(null)
        return min1.x < max2.x && max1.x > min2.x && min1.y < max2.y && max1.y > min2.y && min1.z < max2.z && max1.z > min2.z
    }
}