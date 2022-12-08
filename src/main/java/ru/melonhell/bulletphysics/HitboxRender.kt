package ru.melonhell.bulletphysics

import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import org.bukkit.*
import org.bukkit.Particle.DustOptions
import org.bukkit.plugin.java.JavaPlugin
import ru.melonhell.bulletphysics.bullet.collision.body.element.PhysicsElement
import ru.melonhell.bulletphysics.bullet.collision.body.shape.Triangle
import ru.melonhell.bulletphysics.init.BulletPhysicsPlugin
import ru.melonhell.bulletphysics.utils.math.MathShit
import java.util.*

object HitboxRender {
    @JvmStatic
    fun render(physicsElement: PhysicsElement, world: World) {
        Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getPlugin(
            BulletPhysicsPlugin::class.java
        ), Runnable { renderSync(physicsElement, world) })
    }

    @JvmStatic
    fun renderSync(physicsElement: PhysicsElement, world: World) {
        val rigidBody = physicsElement.rigidBody
        val position = rigidBody.motionState.getLocation(Vector3f())
        val orientation = rigidBody.motionState.getOrientation(Quaternion())
        val triangles = physicsElement.minecraftShape.getTriangles(Quaternion.IDENTITY)
        for (triangle in triangles) {
            triangle(triangle, world, orientation, position, false)
        }
    }

    @JvmStatic
    fun triangle(triangle: Triangle, world: World, orientation: Quaternion, position: Vector3f, lines: Boolean) {
        val vertices = Arrays.stream(triangle.transform(orientation).vertices)
            .map { vector3f: Vector3f -> vector3f.add(position) }
            .toList()
        for (i in 0..2) {
            val v1 = vertices[i]
            val v2 = vertices[(i + 1) % 3]
            if (lines) line(v1, v2, 3, world)
            particle(Location(world, v1.x.toDouble(), v1.y.toDouble(), v1.z.toDouble()), Color.RED)
        }
    }

    @JvmStatic
    fun line(v1: Vector3f, v2: Vector3f, count: Int, world: World) {
        for (i in 1 until count) {
            val lerp = MathShit.lerp(v1, v2, i.toFloat() / count)
            particle(Location(world, lerp.x.toDouble(), lerp.y.toDouble(), lerp.z.toDouble()), Color.YELLOW)
        }
    }

    @JvmStatic
    fun particle(location: Location, color: Color?) {
        location.world.spawnParticle(
            Particle.REDSTONE, location, 1, 0.0, 0.0, 0.0, DustOptions(
                color!!, 1f
            )
        )
    }
}