package ru.melonhell.bulletphysics.bullet.collision.body.shape

import com.jme3.bounding.BoundingBox
import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import kotlin.math.sign

class Triangle(
    v1: Vector3f,
    v2: Vector3f,
    v3: Vector3f
) {
    val vertices: Array<Vector3f>
    val centroid: Vector3f
    val area: Vector3f

    init {
        vertices = arrayOf(v1, v2, v3)
        centroid = Vector3f().add(v1).add(v2).add(v3).divideLocal(3.0f)
        val e1 = v1.subtract(v2)
        val e2 = v2.subtract(v3)
        area = e2.cross(e1).multLocal(0.5f)
        area.multLocal(sign(centroid.dot(area))) // make sure it faces outward
    }

    fun transform(quaternion: Quaternion): Triangle {
        return Triangle(
            transform(vertices[0].clone(), quaternion),
            transform(vertices[1].clone(), quaternion),
            transform(vertices[2].clone(), quaternion)
        )
    }

    companion object {
        @JvmStatic
        fun getMeshOf(boundingBox: BoundingBox): List<Triangle> {
            val triangles = ArrayList<Triangle>()
            for (quad in getQuads(boundingBox)) {
                triangles.add(Triangle(quad.p0, quad.p1, quad.p2))
                triangles.add(Triangle(quad.p2, quad.p3, quad.p0))
            }
            return triangles
        }

        private fun getQuads(box: BoundingBox): List<Quad> {
            val min = box.getMin(null)
            val max = box.getMax(null)
            return java.util.List.of(
                Quad(
                    Quad.Side.NORTH,
                    Vector3f(min.x, max.y, min.z),
                    Vector3f(max.x, max.y, min.z),
                    Vector3f(max.x, min.y, min.z),
                    Vector3f(min.x, min.y, min.z)
                ),
                Quad(
                    Quad.Side.EAST,
                    Vector3f(max.x, max.y, min.z),
                    Vector3f(max.x, max.y, max.z),
                    Vector3f(max.x, min.y, max.z),
                    Vector3f(max.x, min.y, min.z)
                ),
                Quad(
                    Quad.Side.SOUTH,
                    Vector3f(max.x, max.y, max.z),
                    Vector3f(min.x, max.y, max.z),
                    Vector3f(min.x, min.y, max.z),
                    Vector3f(max.x, min.y, max.z)
                ),
                Quad(
                    Quad.Side.WEST,
                    Vector3f(min.x, max.y, max.z),
                    Vector3f(min.x, max.y, min.z),
                    Vector3f(min.x, min.y, min.z),
                    Vector3f(min.x, min.y, max.z)
                ),
                Quad(
                    Quad.Side.UP,
                    Vector3f(max.x, max.y, min.z),
                    Vector3f(min.x, max.y, min.z),
                    Vector3f(min.x, max.y, max.z),
                    Vector3f(max.x, max.y, max.z)
                ),
                Quad(
                    Quad.Side.DOWN,
                    Vector3f(max.x, min.y, max.z),
                    Vector3f(min.x, min.y, max.z),
                    Vector3f(min.x, min.y, min.z),
                    Vector3f(max.x, min.y, min.z)
                )
            )
        }

        @JvmStatic
        private fun transform(vector: Vector3f, rotation: Quaternion): Vector3f {
            val quaternion = Quaternion(rotation)
            quaternion.multLocal(Quaternion(vector.x, vector.y, vector.z, 0.0f))
            val quaternion2 = Quaternion(-rotation.x, -rotation.y, -rotation.z, rotation.w)
            quaternion.multLocal(quaternion2)
            return Vector3f(quaternion.x, quaternion.y, quaternion.z)
        }
    }
}