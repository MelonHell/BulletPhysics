package ru.melonhell.bulletphysics.bullet.collision.space.cache.block.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.block.BlockState;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.BlockCache;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data.BlockDataUtils;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data.PhysicsBlockData;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos;

import java.util.concurrent.TimeUnit;

public class GuavaBlockCache implements BlockCache {
    @Getter
    private final MinecraftSpace space;
    private final BlockDataUtils blockDataUtils;
    private final LoadingCache<BlockPos, PhysicsBlockData> physicsBlockDataCache;

    public GuavaBlockCache(MinecraftSpace space, BlockDataUtils blockDataUtils) {
        this.space = space;
        this.blockDataUtils = blockDataUtils;
        physicsBlockDataCache = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.SECONDS)
                .build(CacheLoader.from(this::loadBlockData));
    }

    @Override
    public void refreshBlockData(BlockPos blockPos, BlockState blockState) {
        physicsBlockDataCache.invalidate(blockPos);
    }

    private PhysicsBlockData loadBlockData(BlockPos blockPos) {
        return blockDataUtils.getPhysicsBlockData(blockPos.getBlock(space.getWorld()));
    }

    @SneakyThrows
    @Override
    public PhysicsBlockData getBlockData(BlockPos blockPos) {
        return physicsBlockDataCache.get(blockPos);
    }
}