package ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.FluidCacheFactory;
import ru.melonhell.bulletphysics.nms.NmsTools;

@Component
@RequiredArgsConstructor
public class GuavaFluidCacheFactory implements FluidCacheFactory {
    private final NmsTools nmsTools;

    @Override
    public GuavaFluidCache create(MinecraftSpace space) {
        return new GuavaFluidCache(space, nmsTools);
    }
}
