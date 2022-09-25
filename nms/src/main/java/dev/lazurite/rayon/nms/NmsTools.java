package dev.lazurite.rayon.nms;

import dev.lazurite.rayon.nms.wrappers.AABBWrapper;
import dev.lazurite.rayon.nms.wrappers.BlockPosWrapper;
import dev.lazurite.rayon.nms.wrappers.FluidStateWrapper;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;

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
