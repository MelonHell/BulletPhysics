package ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.impl

import org.springframework.stereotype.Component
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace
import ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.FluidCacheFactory

@Component
class EmptyFluidCacheFactory : FluidCacheFactory {
    override fun create(space: MinecraftSpace): EmptyFluidCache = EmptyFluidCache()
}