package dev.lazurite.rayon.impl.bullet.math;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.nms.wrappers.AABBWrapper;
import dev.lazurite.rayon.nms.wrappers.BlockPosWrapper;
import dev.lazurite.rayon.nms.wrappers.QuaternionWrapper;
import org.bukkit.util.Vector;

public class Convert {
    public static AABBWrapper toMinecraft(BoundingBox box) {
        var min = box.getMin(new Vector3f());
        var max = box.getMax(new Vector3f());
        return new AABBWrapper(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public static BoundingBox toBullet(AABBWrapper box) {
        return new BoundingBox(new Vector3f((float) box.getMinX(), (float) box.getMinY(), (float) box.getMinZ()), new Vector3f((float) box.getMaxX(), (float) box.getMaxY(), (float) box.getMaxZ()));
    }

    public static com.jme3.math.Quaternion toBullet(QuaternionWrapper quat) {
        return new com.jme3.math.Quaternion(quat.getI(), quat.getJ(), quat.getK(), quat.getR());
    }

    public static Vector3f toBullet(BlockPosWrapper blockPos) {
        return new Vector3f(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f);
    }

    public static QuaternionWrapper toMinecraft(com.jme3.math.Quaternion quat) {
        return new QuaternionWrapper(quat.getX(), quat.getY(), quat.getZ(), quat.getW());
    }

    public static Vector toMinecraft(Vector3f vector3f) {
        return new Vector(vector3f.x, vector3f.y, vector3f.z);
    }

    public static Vector3f toBullet(Vector vector3f) {
        return new Vector3f((float) vector3f.getX(), (float) vector3f.getY(), (float) vector3f.getZ());
    }
}
