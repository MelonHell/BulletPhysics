package ru.melonhell.bulletphysics.utils.math;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MathShit {

    public Quaternion slerp(Quaternion q1, Quaternion q2, float t) {
        q1.normalizeLocal();
        q2.normalizeLocal();

        if (q1.getX() == q2.getX() && q1.getY() == q2.getY() && q1.getZ() == q2.getZ() && q1.getW() == q2.getW()) {
            return new Quaternion(q1.getX(), q1.getY(), q1.getZ(), q1.getW());
        }

        var result = (q1.getX() * q2.getX()) + (q1.getY() * q2.getY()) + (q1.getZ() * q2.getZ()) + (q1.getW() * q2.getW());

        if (result < 0.0f) {
            q2.set(-q2.getX(), -q2.getY(), -q2.getZ(), -q2.getW());
            result = -result;
        }

        var scale0 = 1 - t;
        var scale1 = t;

        if ((1 - result) > 0.1f) {
            final var theta = (float) Math.acos(result);
            final var invSinTheta = 1f / (float) Math.sin(theta);

            scale0 = (float) Math.sin((1 - t) * theta) * invSinTheta;
            scale1 = (float) Math.sin((t * theta)) * invSinTheta;
        }

        final var out = new Quaternion(
                (scale0 * q1.getX()) + (scale1 * q2.getX()),
                (scale0 * q1.getY()) + (scale1 * q2.getY()),
                (scale0 * q1.getZ()) + (scale1 * q2.getZ()),
                (scale0 * q1.getW()) + (scale1 * q2.getW()));

        out.normalizeLocal();
        return out;
    }

    public Vector3f toEulerAngles(Quaternion quat) {
        final var q = new Quaternion(quat);

        var i = 0.0f;
        var j = 0.0f;
        var k = 0.0f;

        // roll (x-axis rotation)
        final var sinr_cosp = 2 * (q.getW() * q.getX() + q.getY() * q.getZ());
        final var cosr_cosp = 1 - 2 * (q.getX() * q.getX() + q.getY() * q.getY());
        i = (float) Math.atan2(sinr_cosp, cosr_cosp);

        // pitch (y-axis rotation)
        final var sinp = 2 * (q.getW() * q.getY() - q.getZ() * q.getX());
        if (Math.abs(sinp) >= 1) j = (float) Math.copySign(Math.PI / 2, sinp); // use 90 degrees if out of range
        else j = (float) Math.asin(sinp);

        // yaw (z-axis rotation)
        final var siny_cosp = 2 * (q.getW() * q.getZ() + q.getX() * q.getY());
        final var cosy_cosp = 1 - 2 * (q.getY() * q.getY() + q.getZ() * q.getZ());
        k = (float) Math.atan2(siny_cosp, cosy_cosp);

        return new Vector3f(i, j, k);
    }

    public double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }

    public Vector3f lerp(Vector3f vec1, Vector3f vec2, float delta) {
        return new Vector3f(
                (float) lerp(delta, vec1.x, vec2.x),
                (float) lerp(delta, vec1.y, vec2.y),
                (float) lerp(delta, vec1.z, vec2.z)
        );
    }
}
