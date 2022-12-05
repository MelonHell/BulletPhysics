package ru.melonhell.bulletphysics.nms

import com.jme3.bounding.BoundingBox
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.util.Vector
import ru.melonhell.bulletphysics.nms.wrappers.FluidStateWrapper

interface NmsTools {
    fun getFluidState(block: Block): FluidStateWrapper
    fun getHeight(fluidState: FluidStateWrapper, block: Block): Float
    fun getFlow(fluidState: FluidStateWrapper, block: Block): Vector
    fun collidableCheck(blockState: BlockState): Boolean
    fun getBlockId(blockState: BlockState): Int
    fun equalsById(blockState1: BlockState, blockState2: BlockState): Boolean
    fun boundingBox(block: Block, blockState: BlockState): BoundingBox?
    fun boundingBoxes(block: Block, blockState: BlockState): List<BoundingBox>
    fun createBlockState(material: Material): BlockState
    fun getBlockState(block: Block): BlockState
}