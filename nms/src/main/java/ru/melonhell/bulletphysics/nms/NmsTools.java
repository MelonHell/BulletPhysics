package ru.melonhell.bulletphysics.nms;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;
import ru.melonhell.bulletphysics.nms.wrappers.AABBWrapper;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPosWrapper;
import ru.melonhell.bulletphysics.nms.wrappers.FluidStateWrapper;

import java.util.stream.Stream;

public interface NmsTools {
    Stream<BlockPosWrapper> betweenClosedStream(AABBWrapper aabb);

    FluidStateWrapper getFluidState(Block block);

    float getHeight(FluidStateWrapper fluidState, Block block);

    Vector getFlow(FluidStateWrapper fluidState, Block block);

    boolean collidableCheck(BlockState blockState);

    int getBlockId(BlockState blockState);

    AABBWrapper boundingBox(Block block);
}
