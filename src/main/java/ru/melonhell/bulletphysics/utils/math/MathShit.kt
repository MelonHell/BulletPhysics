package ru.melonhell.bulletphysics.utils.math

import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import kotlin.math.*

object MathShit {
    @JvmStatic
    fun slerp(q1: Quaternion, q2: Quaternion, t: Float): Quaternion {
        q1.normalizeLocal()
        q2.normalizeLocal()
        if (q1.x == q2.x && q1.y == q2.y && q1.z == q2.z && q1.w == q2.w) {
            return Quaternion(q1.x, q1.y, q1.z, q1.w)
        }
        var result = q1.x * q2.x + q1.y * q2.y + q1.z * q2.z + q1.w * q2.w
        if (result < 0.0f) {
            q2[-q2.x, -q2.y, -q2.z] = -q2.w
            result = -result
        }
        var scale0 = 1 - t
        var scale1 = t
        if (1 - result > 0.1f) {
            val theta = acos(result.toDouble()).toFloat()
            val invSinTheta = 1.0f / sin(theta.toDouble()).toFloat()
            scale0 = sin(((1 - t) * theta).toDouble()).toFloat() * invSinTheta
            scale1 = sin((t * theta).toDouble()).toFloat() * invSinTheta
        }
        val out = Quaternion(
            scale0 * q1.x + scale1 * q2.x,
            scale0 * q1.y + scale1 * q2.y,
            scale0 * q1.z + scale1 * q2.z,
            scale0 * q1.w + scale1 * q2.w
        )
        out.normalizeLocal()
        return out
    }

    @JvmStatic
    fun toEulerAngles(quat: Quaternion?): Vector3f {
        val q = Quaternion(quat)
        var i = 0.0f
        var j = 0.0f
        var k = 0.0f
        // roll (x-axis rotation)
        val sinrCosp = 2 * (q.w * q.x + q.y * q.z)
        val cosrCosp = 1 - 2 * (q.x * q.x + q.y * q.y)
        i = atan2(sinrCosp.toDouble(), cosrCosp.toDouble()).toFloat()
        // pitch (y-axis rotation)
        val sinp = 2 * (q.w * q.y - q.z * q.x)
        j = if (abs(sinp) >= 1) (Math.PI / 2).withSign(sinp.toDouble())
            .toFloat() // use 90 degrees if out of range
        else asin(sinp.toDouble()).toFloat()
        // yaw (z-axis rotation)
        val sinyCosp = 2 * (q.w * q.z + q.x * q.y)
        val cosyCosp = 1 - 2 * (q.y * q.y + q.z * q.z)
        k = atan2(sinyCosp.toDouble(), cosyCosp.toDouble()).toFloat()
        return Vector3f(i, j, k)
    }

    fun lerp(delta: Double, start: Double, end: Double): Double {
        return start + delta * (end - start)
    }

    @JvmStatic
    fun lerp(vec1: Vector3f, vec2: Vector3f, delta: Float): Vector3f {
        return Vector3f(
            lerp(delta.toDouble(), vec1.x.toDouble(), vec2.x.toDouble()).toFloat(),
            lerp(delta.toDouble(), vec1.y.toDouble(), vec2.y.toDouble()).toFloat(),
            lerp(delta.toDouble(), vec1.z.toDouble(), vec2.z.toDouble()).toFloat()
        )
    }
}