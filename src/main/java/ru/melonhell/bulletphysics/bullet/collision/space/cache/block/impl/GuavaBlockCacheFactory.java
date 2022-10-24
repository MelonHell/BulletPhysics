package ru.melonhell.bulletphysics.bullet.collision.space.cache.block.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.BlockCacheFactory;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data.BlockDataUtils;

@Component
@RequiredArgsConstructor
public class GuavaBlockCacheFactory implements BlockCacheFactory {
    private final BlockDataUtils blockDataUtils;

    @Override
    public GuavaBlockCache create(MinecraftSpace space) {
        return new GuavaBlockCache(space, blockDataUtils);
    }
}
