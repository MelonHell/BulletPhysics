package ru.melonhell.bulletphysics.nms;

import com.jme3.bounding.BoundingBox;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;
import ru.melonhell.bulletphysics.nms.wrappers.FluidStateWrapper;

import java.util.List;

public interface NmsTools {

    FluidStateWrapper getFluidState(Block block);

    float getHeight(FluidStateWrapper fluidState, Block block);

    Vector getFlow(FluidStateWrapper fluidState, Block block);

    boolean collidableCheck(BlockState blockState);

    int getBlockId(BlockState blockState);

    boolean equalsById(BlockState blockState1, BlockState blockState2);

    BoundingBox boundingBox(Block block, BlockState blockState);

    List<BoundingBox> boundingBoxes(Block block, BlockState blockState);

    BlockState createBlockState(Material material);
}
