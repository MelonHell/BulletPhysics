package ru.melonhell.bulletphysics.utils.math

import com.jme3.math.Vector3f
import org.bukkit.util.Vector
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos

object Convert {
    fun toBullet(blockPos: BlockPos): Vector3f {
        return Vector3f(blockPos.x + 0.5f, blockPos.y + 0.5f, blockPos.z + 0.5f)
    }

    fun toMinecraft(vector3f: Vector3f): Vector {
        return Vector(vector3f.x, vector3f.y, vector3f.z)
    }

    @JvmStatic
    fun toBullet(vector3f: Vector): Vector3f {
        return Vector3f(vector3f.x.toFloat(), vector3f.y.toFloat(), vector3f.z.toFloat())
    }
}