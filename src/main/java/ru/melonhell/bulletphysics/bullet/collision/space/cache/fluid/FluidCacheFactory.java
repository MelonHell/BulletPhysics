package ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid;

import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;

public interface FluidCacheFactory {
    FluidCache create(MinecraftSpace space);
}
