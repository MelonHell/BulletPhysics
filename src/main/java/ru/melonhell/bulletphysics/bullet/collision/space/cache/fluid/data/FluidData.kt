package ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.data

import org.bukkit.block.Block
import ru.melonhell.bulletphysics.nms.wrappers.FluidStateWrapper

data class FluidData(
    val block: Block,
    val fluidState: FluidStateWrapper
)