package ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data

import org.bukkit.block.Block
import org.bukkit.block.BlockState
import ru.melonhell.bulletphysics.bullet.collision.body.shape.MinecraftShape

data class PhysicsBlockData(
    val block: Block,
    val blockState: BlockState,
    val shape: MinecraftShape?
)