package ru.melonhell.bulletphysics.bullet.collision.space.cache.block.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.BlockCacheFactory;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data.BlockDataUtils;
import ru.melonhell.bulletphysics.nms.NmsTools;

@Component
@RequiredArgsConstructor
public class DirectBlockCacheFactory implements BlockCacheFactory {
    private final NmsTools nmsTools;
    private final BlockDataUtils blockDataUtils;

    @Override
    public DirectBlockCache create(MinecraftSpace space) {
        return new DirectBlockCache(space, blockDataUtils);
    }
}
