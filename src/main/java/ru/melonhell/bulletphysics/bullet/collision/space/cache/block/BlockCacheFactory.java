package ru.melonhell.bulletphysics.bullet.collision.space.cache.block;

import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;

public interface BlockCacheFactory {
    BlockCache create(MinecraftSpace space);
}
