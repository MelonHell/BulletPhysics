package ru.melonhell.bulletphysics.bullet.collision.space.cache.data;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import ru.melonhell.bulletphysics.bullet.collision.body.shape.MinecraftShape;

public record BlockData(Block block, BlockState blockState, MinecraftShape shape) {
}
