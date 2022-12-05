package ru.melonhell.bulletphysics.bullet.collision.space

import org.bukkit.World
import org.springframework.stereotype.Component
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.impl.GuavaBlockCacheFactory
import ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.impl.GuavaFluidCacheFactory
import ru.melonhell.bulletphysics.bullet.collision.space.generator.PressureGenerator
import ru.melonhell.bulletphysics.bullet.collision.space.generator.TerrainGenerator
import ru.melonhell.bulletphysics.bullet.thread.PhysicsThread

@Component
class SpaceService(
    private val physicsThread: PhysicsThread,
    private val terrainGenerator: TerrainGenerator,
    private val pressureGenerator: PressureGenerator,
    private val blockCacheFactory: GuavaBlockCacheFactory,
    private val fluidCacheFactory: GuavaFluidCacheFactory
) {
    private val spaceMap: MutableMap<World, MinecraftSpace> = HashMap()
    operator fun get(world: World): MinecraftSpace {
        val minecraftSpace = spaceMap[world]
        if (minecraftSpace == null) {
            val space = MinecraftSpace(
                physicsThread,
                blockCacheFactory,
                fluidCacheFactory,
                terrainGenerator,
                pressureGenerator,
                world
            )
            set(world, space)
            return space
        }
        return minecraftSpace
    }

    val all: Collection<MinecraftSpace>
        get() = spaceMap.values

    operator fun set(level: World, minecraftSpace: MinecraftSpace) {
        spaceMap[level] = minecraftSpace
    }
}