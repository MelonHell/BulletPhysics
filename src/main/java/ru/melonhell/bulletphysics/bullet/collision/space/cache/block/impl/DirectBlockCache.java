package ru.melonhell.bulletphysics.bullet.collision.space.cache.block.impl;

import lombok.Getter;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.BlockCache;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data.BlockDataUtils;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data.PhysicsBlockData;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos;

public class DirectBlockCache implements BlockCache {
    @Getter
    private final MinecraftSpace space;
    private final BlockDataUtils blockDataUtils;

    public DirectBlockCache(MinecraftSpace space, BlockDataUtils blockDataUtils) {
        this.space = space;
        this.blockDataUtils = blockDataUtils;
    }

    @Override
    public PhysicsBlockData getBlockData(BlockPos blockPos) {
        return blockDataUtils.getPhysicsBlockData(blockPos.getBlock(space.getWorld()));
    }
}