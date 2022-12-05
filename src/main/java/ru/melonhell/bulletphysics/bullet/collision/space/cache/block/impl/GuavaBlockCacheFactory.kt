package ru.melonhell.bulletphysics.bullet.collision.space.cache.block.impl

import org.springframework.stereotype.Component
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.BlockCacheFactory
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data.BlockDataUtils

@Component
class GuavaBlockCacheFactory(
    private val blockDataUtils: BlockDataUtils
) : BlockCacheFactory {
    override fun create(space: MinecraftSpace): GuavaBlockCache {
        return GuavaBlockCache(space, blockDataUtils)
    }
}