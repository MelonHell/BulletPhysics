package ru.melonhell.bulletphysics.utils.math;

import com.jme3.math.Vector3f;
import org.bukkit.util.Vector;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPosWrapper;

public class Convert {
    public static Vector3f toBullet(BlockPosWrapper blockPos) {
        return new Vector3f(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f);
    }

    public static Vector toMinecraft(Vector3f vector3f) {
        return new Vector(vector3f.x, vector3f.y, vector3f.z);
    }

    public static Vector3f toBullet(Vector vector3f) {
        return new Vector3f((float) vector3f.getX(), (float) vector3f.getY(), (float) vector3f.getZ());
    }
}
