package ru.melonhell.bulletphysics.bullet.collision.space.cache.block

import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace

interface BlockCacheFactory {
    fun create(space: MinecraftSpace): BlockCache
}