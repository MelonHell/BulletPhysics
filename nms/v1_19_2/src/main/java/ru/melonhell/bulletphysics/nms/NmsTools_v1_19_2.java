package ru.melonhell.bulletphysics.nms;

import com.jme3.bounding.BoundingBox;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlockStates;
import org.bukkit.util.Vector;
import org.springframework.stereotype.Component;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos;
import ru.melonhell.bulletphysics.nms.wrappers.FluidStateWrapper;

import java.util.List;
import java.util.stream.Stream;

@Component
public class NmsTools_v1_19_2 implements NmsTools {


    @Override
    public Stream<BlockPos> betweenClosedStream(BoundingBox aabb) {
        return net.minecraft.core.BlockPos.betweenClosedStream(NmsWrapUtils_v1_19_2.convert(aabb)).map(blockPos -> new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
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
        if (handle.isAir()) return false;
        if (!handle.getMaterial().isSolid() && !handle.getMaterial().isLiquid()) return false;
        return handle.getFluidState().isEmpty() || (handle.hasProperty(BlockStateProperties.WATERLOGGED) && handle.getValue(BlockStateProperties.WATERLOGGED));
    }

    @Override
    public int getBlockId(BlockState blockState) {
        return net.minecraft.world.level.block.Block.getId(((CraftBlockState) blockState).getHandle());
    }

    @Override
    public BoundingBox boundingBox(Block block, BlockState blockState) {
        net.minecraft.world.level.block.state.BlockState stateHandle = ((CraftBlockState) blockState).getHandle();
        VoxelShape voxelShape = stateHandle.getCollisionShape(((CraftBlock) block).getHandle(), ((CraftBlock) block).getPosition());
//        AABB boundingBox = voxelShape.isEmpty() ? new AABB(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f) : voxelShape.bounds();
        if (voxelShape.isEmpty()) return null;
        return NmsWrapUtils_v1_19_2.convert(voxelShape.bounds());
    }

    @Override
    public List<BoundingBox> boundingBoxes(BlockPos blockPos, World world, BlockState blockState) {
        net.minecraft.world.level.block.state.BlockState stateHandle = ((CraftBlockState) blockState).getHandle();
        net.minecraft.core.BlockPos blockPosHandle = new net.minecraft.core.BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        ServerLevel worldHandle = ((CraftWorld) world).getHandle();
        VoxelShape voxelShape = stateHandle.getCollisionShape(worldHandle, blockPosHandle);
        List<AABB> boundingBoxes = voxelShape.toAabbs();
        return boundingBoxes.stream().map(NmsWrapUtils_v1_19_2::convert).toList();
    }

    @Override
    public BlockState createBlockState(Material material) {
        return CraftBlockStates.getBlockState(material, null);
    }
}
