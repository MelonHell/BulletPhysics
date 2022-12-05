package ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.impl

import org.springframework.stereotype.Component
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace
import ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.FluidCacheFactory
import ru.melonhell.bulletphysics.nms.NmsTools

@Component
class GuavaFluidCacheFactory(private val nmsTools: NmsTools) : FluidCacheFactory {
    override fun create(space: MinecraftSpace): GuavaFluidCache = GuavaFluidCache(space, nmsTools)
}