package ru.melonhell.bulletphysics.nms

import com.jme3.bounding.BoundingBox
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.craftbukkit.v1_19_R3.block.CraftBlock
import org.bukkit.craftbukkit.v1_19_R3.block.CraftBlockState
import org.bukkit.craftbukkit.v1_19_R3.block.CraftBlockStates
import org.bukkit.util.Vector
import org.springframework.stereotype.Component
import ru.melonhell.bulletphysics.nms.wrappers.FluidStateWrapper

@Component
class NmsTools_v1_19_4 : NmsTools {
    override fun getFluidState(block: Block): FluidStateWrapper {
        val fluidState = (block as CraftBlock?)!!.handle.getFluidState(block.position)
        return NmsWrapUtils_v1_19_4.wrap(fluidState)
    }

    override fun getHeight(fluidState: FluidStateWrapper, block: Block): Float {
        return NmsWrapUtils_v1_19_4.unwrap(fluidState).getHeight((block as CraftBlock).handle, block.position)
    }

    override fun getFlow(fluidState: FluidStateWrapper, block: Block): Vector {
        val flow: Vec3 = NmsWrapUtils_v1_19_4.unwrap(fluidState).getFlow((block as CraftBlock).handle, block.position)
        return Vector(flow.x, flow.y, flow.z)
    }

    override fun collidableCheck(blockState: BlockState): Boolean {
        val handle = (blockState as CraftBlockState).handle
        if (handle.isAir) return false
        return if (!handle.material.isSolid && !handle.material.isLiquid) false else handle.fluidState.isEmpty || handle.hasProperty(
            BlockStateProperties.WATERLOGGED
        ) && handle.getValue(
            BlockStateProperties.WATERLOGGED
        )
    }

    override fun getBlockId(blockState: BlockState): Int {
        return net.minecraft.world.level.block.Block.getId((blockState as CraftBlockState?)!!.handle)
    }

    override fun equalsById(blockState1: BlockState, blockState2: BlockState): Boolean {
        return getBlockId(blockState1) == getBlockId(blockState2)
    }

    override fun boundingBox(block: Block, blockState: BlockState): BoundingBox? {
        val stateHandle = (blockState as CraftBlockState).handle
        val voxelShape = stateHandle.getCollisionShape((block as CraftBlock).handle, block.position)
        return if (voxelShape.isEmpty) null else NmsWrapUtils_v1_19_4.convert(
            voxelShape.bounds()
        )
    }

    override fun boundingBoxes(block: Block, blockState: BlockState): List<BoundingBox> {
        val stateHandle = (blockState as CraftBlockState).handle
        val blockPosHandle = (block as CraftBlock).position
        val worldHandle = block.handle
        val voxelShape = stateHandle.getCollisionShape(worldHandle, blockPosHandle)
        val boundingBoxes = voxelShape.toAabbs()
        return boundingBoxes.map { obj: AABB -> NmsWrapUtils_v1_19_4.convert(obj) }
    }

    override fun createBlockState(material: Material): BlockState {
        return CraftBlockStates.getBlockState(material, null)
    }

    override fun getBlockState(block: Block): BlockState {
        val worldHandle = (block as CraftBlock).handle
        val blockPosHandle = block.position
        return CraftBlockStates.getBlockState(worldHandle, blockPosHandle)
    }
}