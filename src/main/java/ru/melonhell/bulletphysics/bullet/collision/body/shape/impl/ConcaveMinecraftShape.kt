package ru.melonhell.bulletphysics.bullet.collision.body.shape.impl

import com.jme3.bounding.BoundingBox
import com.jme3.bullet.collision.shapes.MeshCollisionShape
import com.jme3.bullet.collision.shapes.infos.IndexedMesh
import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import ru.melonhell.bulletphysics.bullet.collision.body.shape.MinecraftShape
import ru.melonhell.bulletphysics.bullet.collision.body.shape.Triangle
import java.util.function.Supplier
import java.util.stream.Stream

/* Less stable :( */
class ConcaveMinecraftShape(private val triangles: List<Triangle>) : MeshCollisionShape(
    false,
    Supplier {
        val vertices = triangles.stream()
            .flatMap { triangle: Triangle -> Stream.of(*triangle.vertices) }
            .toList().toTypedArray()
        val indices = IntArray(vertices.size)
        for (i in vertices.indices) {
            indices[i] = i
        }
        IndexedMesh(vertices, indices)
    }.get()
), MinecraftShape {
    override fun getTriangles(quaternion: Quaternion): List<Triangle> {
        return triangles.stream().map { triangle: Triangle ->
            triangle.transform(
                quaternion
            )
        }.toList()
    }

    override val volume: Float
        get() {
            val box = boundingBox(Vector3f(), Quaternion(), BoundingBox())
            return box.xExtent * box.yExtent * box.zExtent
        }

    companion object {
        fun new(box: BoundingBox): ConcaveMinecraftShape {
            return ConcaveMinecraftShape(Triangle.getMeshOf(box))
        }

        fun new(boxes: Iterable<BoundingBox>): ConcaveMinecraftShape {
            val triangles: MutableList<Triangle> = ArrayList()
            for (box in boxes) {
                triangles.addAll(Triangle.getMeshOf(box))
            }
            return ConcaveMinecraftShape(triangles)
        }
    }
}