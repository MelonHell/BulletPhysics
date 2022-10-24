package ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.impl;

import org.springframework.stereotype.Component;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.FluidCacheFactory;

@Component
public class EmptyFluidCacheFactory implements FluidCacheFactory {
    @Override
    public EmptyFluidCache create(MinecraftSpace space) {
        return new EmptyFluidCache();
    }
}
