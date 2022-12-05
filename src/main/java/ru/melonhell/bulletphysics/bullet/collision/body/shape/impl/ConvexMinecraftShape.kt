package ru.melonhell.bulletphysics.bullet.collision.body.shape.impl

import com.jme3.bounding.BoundingBox
import com.jme3.bullet.collision.shapes.HullCollisionShape
import com.jme3.math.Quaternion
import ru.melonhell.bulletphysics.bullet.collision.body.shape.MinecraftShape
import ru.melonhell.bulletphysics.bullet.collision.body.shape.Triangle
import java.util.stream.Stream

class ConvexMinecraftShape(private val triangles: List<Triangle>) : HullCollisionShape(
    triangles.stream().flatMap { triangle: Triangle -> Stream.of(*triangle.vertices) }
        .toList()), MinecraftShape {
    override fun getTriangles(quaternion: Quaternion): List<Triangle> {
        return triangles.stream().map { triangle: Triangle ->
            triangle.transform(
                quaternion
            )
        }.toList()
    }

    override val volume: Float
        get() = aabbVolume()


    companion object {
        fun new(box: BoundingBox): ConvexMinecraftShape {
            return ConvexMinecraftShape(Triangle.getMeshOf(box))
        }

        fun new(boxes: Iterable<BoundingBox>): ConvexMinecraftShape {
            val triangles: MutableList<Triangle> = ArrayList()
            for (box in boxes) {
                triangles.addAll(Triangle.getMeshOf(box))
            }
            return ConvexMinecraftShape(triangles)
        }
    }
}