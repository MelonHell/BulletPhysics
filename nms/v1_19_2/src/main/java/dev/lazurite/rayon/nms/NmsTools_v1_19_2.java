package dev.lazurite.rayon.nms;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import dev.lazurite.rayon.nms.toolbox.QuaternionHelper_v1_19_2;
import dev.lazurite.rayon.nms.toolbox.VectorHelper_v1_19_2;
import dev.lazurite.rayon.nms.wrappers.AABBWrapper;
import dev.lazurite.rayon.nms.wrappers.BlockPosWrapper;
import dev.lazurite.rayon.nms.wrappers.FluidStateWrapper;
import dev.lazurite.rayon.nms.wrappers.QuaternionWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlockState;
import org.bukkit.util.Vector;

import java.util.stream.Stream;

public class NmsTools_v1_19_2 implements NmsTools {
    @Override
    public Stream<BlockPosWrapper> betweenClosedStream(AABBWrapper aabb) {
        return BlockPos.betweenClosedStream(NmsWrapUtils_v1_19_2.unwrap(aabb)).map(blockPos -> new BlockPosWrapper(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
    }

    @Override
    public boolean intersects(AABBWrapper aabb1, AABBWrapper aabb2) {
        return NmsWrapUtils_v1_19_2.unwrap(aabb1).intersects(NmsWrapUtils_v1_19_2.unwrap(aabb2));
    }

    @Override
    public FluidStateWrapper getFluidState(Block block) {
        FluidState fluidState = ((CraftBlock) block).getHandle().getFluidState(((CraftBlock) block).getPosition());
        return NmsWrapUtils_v1_19_2.wrap(fluidState);
    }

    @Override
    public float getHeight(FluidStateWrapper fluidState, Block block) {
        return NmsWrapUtils_v1_19_2.unwrap(fluidState).getHeight(((CraftBlock) block).getHandle(), ((CraftBlock) block).getPosition());
    }

    @Override
    public Vector getFlow(FluidStateWrapper fluidState, Block block) {
        Vec3 flow = NmsWrapUtils_v1_19_2.unwrap(fluidState).getFlow(((CraftBlock) block).getHandle(), ((CraftBlock) block).getPosition());
        return new Vector(flow.x, flow.y, flow.z);
    }

    @Override
    public boolean collidableCheck(BlockState blockState) {
        net.minecraft.world.level.block.state.BlockState handle = ((CraftBlockState) blockState).getHandle();
        return !handle.isAir() && !handle.getBlock().isPossibleToRespawnInThis() && (handle.getFluidState().isEmpty() || (handle.hasProperty(BlockStateProperties.WATERLOGGED) && handle.getValue(BlockStateProperties.WATERLOGGED)));
    }
    @Override
    public Vector transform(Vector vector, QuaternionWrapper quaternion) {
        Vector3f vector3f = new Vector3f((float) vector.getX(), (float) vector.getY(), (float) vector.getZ());
        vector3f.transform(NmsWrapUtils_v1_19_2.unwrap(quaternion));
        return new Vector(vector3f.x(), vector3f.y(), vector3f.z());
    }

    @Override
    public int getBlockId(BlockState blockState) {
        return net.minecraft.world.level.block.Block.getId(((CraftBlockState) blockState).getHandle());
    }

    @Override
    public AABBWrapper boundingBox(Block block) {
        net.minecraft.world.level.block.state.BlockState stateHandle = ((CraftBlockState) block.getState()).getHandle();
        VoxelShape voxelShape = stateHandle.getCollisionShape(((CraftBlock) block).getHandle(), ((CraftBlock) block).getPosition());
        AABB boundingBox = voxelShape.isEmpty() ? new AABB(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f) : voxelShape.bounds();
        return NmsWrapUtils_v1_19_2.wrap(boundingBox);
    }

    @Override
    public Vector getRotationDelta(QuaternionWrapper tickRotation, QuaternionWrapper prevRotation) {
        final var tickAngles = QuaternionHelper_v1_19_2.toEulerAngles(NmsWrapUtils_v1_19_2.unwrap(tickRotation));
        final var prevAngles = QuaternionHelper_v1_19_2.toEulerAngles(NmsWrapUtils_v1_19_2.unwrap(prevRotation));
        tickAngles.sub(prevAngles);
        return new Vector(tickAngles.x(), tickAngles.y(), tickAngles.z());
    }

    @Override
    public Vector lerp(Vector vector1, Vector vector2, float delta) {
        return VectorHelper_v1_19_2.lerp(vector1, vector2, delta);
    }

    @Override
    public QuaternionWrapper slerp(QuaternionWrapper quaternion1, QuaternionWrapper quaternion2, float delta) {
        Quaternion slerp = QuaternionHelper_v1_19_2.slerp(NmsWrapUtils_v1_19_2.unwrap(quaternion1), NmsWrapUtils_v1_19_2.unwrap(quaternion2), delta);
        return NmsWrapUtils_v1_19_2.wrap(slerp);
    }
}
