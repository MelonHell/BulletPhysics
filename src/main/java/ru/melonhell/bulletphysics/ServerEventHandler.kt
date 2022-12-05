package ru.melonhell.bulletphysics

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import ru.melonhell.bulletphysics.bullet.collision.space.SpaceService
import ru.melonhell.bulletphysics.bullet.collision.space.generator.EntityCollisionGenerator
import ru.melonhell.bulletphysics.bullet.thread.PhysicsThread
import ru.melonhell.bulletphysics.nms.NmsTools
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos
import ru.spliterash.springspigot.listener.SpigotListener
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@SpigotListener
class ServerEventHandler(
    private val javaPlugin: JavaPlugin,
    private val spaceService: SpaceService,
    private val physicsThread: PhysicsThread,
    private val nmsTools: NmsTools
) : Listener {
    private var bukkitTask: BukkitTask? = null
    @PostConstruct
    private fun init() {
        bukkitTask = Bukkit.getScheduler().runTaskTimer(javaPlugin, Runnable {
            for (space in spaceService.all) {
                space.step()
                EntityCollisionGenerator.step(space)
            }
            if (physicsThread.throwable != null) {
                throw RuntimeException(physicsThread.throwable)
            }
        }, 0, 1)
    }

    @PreDestroy
    private fun destroy() {
        bukkitTask!!.cancel()
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        blockUpdate(event.block, event.block.state)
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        blockUpdate(event.block, nmsTools.createBlockState(Material.AIR))
    }

    private fun blockUpdate(block: Block, blockState: BlockState) {
        spaceService[block.world].doBlockUpdate(BlockPos(block), blockState)
    }
}