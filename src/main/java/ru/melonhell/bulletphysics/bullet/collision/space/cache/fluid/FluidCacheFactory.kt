package ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid

import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace

interface FluidCacheFactory {
    fun create(space: MinecraftSpace): FluidCache
}