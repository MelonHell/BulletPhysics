package dev.lazurite.rayon.nms;

import dev.lazurite.rayon.nms.wrappers.AABBWrapper;
import dev.lazurite.rayon.nms.wrappers.BlockPosWrapper;
import dev.lazurite.rayon.nms.wrappers.FluidStateWrapper;
import dev.lazurite.rayon.nms.wrappers.QuaternionWrapper;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;

import java.util.stream.Stream;

public interface NmsTools {
    Stream<BlockPosWrapper> betweenClosedStream(AABBWrapper aabb);

    boolean intersects(AABBWrapper aabb1, AABBWrapper aabb2);

    FluidStateWrapper getFluidState(Block block);

    float getHeight(FluidStateWrapper fluidState, Block block);

    Vector getFlow(FluidStateWrapper fluidState, Block block);

    boolean collidableCheck(BlockState blockState);

    Vector transform(Vector vector, QuaternionWrapper quaternion);

    int getBlockId(BlockState blockState);

    AABBWrapper boundingBox(Block block);

    Vector getRotationDelta(QuaternionWrapper tickRotation, QuaternionWrapper prevRotation);

    Vector lerp(Vector vector1, Vector vector2, float delta);

    QuaternionWrapper slerp(QuaternionWrapper quaternion1, QuaternionWrapper quaternion2, float delta);
}
