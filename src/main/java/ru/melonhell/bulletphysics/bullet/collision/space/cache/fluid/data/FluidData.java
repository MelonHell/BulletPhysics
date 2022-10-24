package ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.data;

import org.bukkit.block.Block;
import ru.melonhell.bulletphysics.nms.wrappers.FluidStateWrapper;

public record FluidData(Block block, FluidStateWrapper fluidState) {
}
