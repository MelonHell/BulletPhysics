package ru.melonhell.bulletphysics

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import com.jme3.bounding.BoundingBox
import com.jme3.bullet.objects.PhysicsRigidBody
import com.jme3.math.Vector3f
import org.bukkit.entity.Player
import org.springframework.stereotype.Component
import ru.melonhell.bulletphysics.bullet.collision.body.element.PhysicsElement
import ru.melonhell.bulletphysics.bullet.collision.body.shape.MinecraftShape
import ru.melonhell.bulletphysics.bullet.collision.body.shape.impl.ConvexMinecraftShape
import ru.melonhell.bulletphysics.bullet.collision.space.SpaceService

@Component
@CommandAlias("bulletphysics")
class TestCommand(private val spaceService: SpaceService) : BaseCommand() {
    @Subcommand("test")
    fun test(player: Player, @Default("1") size: Float, @Default("1") count: Int) {
        for (i in 0 until count) {
            val space = spaceService[player.world]
            val convex = ConvexMinecraftShape.new(BoundingBox(Vector3f(), size * 0.5f, size * 0.5f, size * 0.5f))
            val rigidBody = PhysicsRigidBody(convex, 100f)
            val physicsElement: PhysicsElement = object : PhysicsElement(rigidBody) {
                override fun updateFrame() {
                    super.updateFrame()
                    HitboxRender.render(this, player.world)
                }
            }
            val location = player.location
            rigidBody.setPhysicsLocation(Vector3f(location.x.toFloat(), location.y.toFloat(), location.z.toFloat()))
            space.addPhysicsElement(physicsElement)
        }
    }
}