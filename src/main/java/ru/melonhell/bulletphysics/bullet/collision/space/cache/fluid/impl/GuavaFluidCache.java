package ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.SneakyThrows;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.FluidCache;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.data.FluidColumn;
import ru.melonhell.bulletphysics.nms.NmsTools;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class GuavaFluidCache implements FluidCache {
    private final MinecraftSpace space;
    private final NmsTools nmsTools;
    private final LoadingCache<BlockPos, Optional<FluidColumn>> fluidDataCache;

    public GuavaFluidCache(MinecraftSpace space, NmsTools nmsTools) {
        this.space = space;
        this.nmsTools = nmsTools;
        this.fluidDataCache = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.SECONDS)
                .build(CacheLoader.from(this::loadFluidData));
    }

    @Override
    public void refreshFluidData(BlockPos blockPos) {
        fluidDataCache.invalidate(blockPos);
    }

    private Optional<FluidColumn> loadFluidData(BlockPos blockPos) {
        final var world = space.getWorld();
        if (!nmsTools.getFluidState(blockPos.getBlock(world)).isEmpty()) {
            return Optional.of(new FluidColumn(blockPos.getBlock(world), nmsTools));
        }
        return Optional.empty();
    }

    @SneakyThrows
    @Override
    public FluidColumn getFluidColumn(BlockPos blockPos) {
        return fluidDataCache.get(blockPos).orElse(null);
    }
}
