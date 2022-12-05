package ru.melonhell.bulletphysics.bullet.collision.body.shape

import com.jme3.math.Vector3f

data class Quad(
    val side: Side,
    val p0: Vector3f,
    val p1: Vector3f,
    val p2: Vector3f,
    val p3: Vector3f
) {
    val points: List<Vector3f>
        get() = listOf(p0, p1, p2, p3)

    enum class Side(val x: Int, val y: Int, val z: Int) {
        NORTH(0, 0, -1), EAST(1, 0, 0), SOUTH(0, 0, 1), WEST(-1, 0, 0), UP(0, 1, 0), DOWN(0, -1, 0);

        fun toVector(): Vector3f {
            return Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
        }
    }
}