package ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data;

import com.jme3.bounding.BoundingBox;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.springframework.stereotype.Component;
import ru.melonhell.bulletphysics.bullet.collision.body.shape.MinecraftShape;
import ru.melonhell.bulletphysics.bullet.collision.space.block.BlockProperty;
import ru.melonhell.bulletphysics.nms.NmsTools;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BlockDataUtils {
    private final NmsTools nmsTools;

    public PhysicsBlockData getPhysicsBlockData(Block block) {
        return getPhysicsBlockData(block, nmsTools.getBlockState(block));
    }

    public PhysicsBlockData getPhysicsBlockData(Block block, BlockState blockState) {
        MinecraftShape shape = null;
        if (isValidBlock(blockState)) {
            List<BoundingBox> boundingBoxes = nmsTools.boundingBoxes(block, blockState);
            if (!boundingBoxes.isEmpty()) {
                shape = MinecraftShape.convex(boundingBoxes);
            }
        }
        return new PhysicsBlockData(block, blockState, shape);
    }

    boolean isValidBlock(BlockState blockState) {
        if (blockState == null) {
            return false;
        }

        final var block = blockState.getType();
        final var properties = BlockProperty.getBlockProperty(block);

        if (properties != null) return properties.collidable();
        return nmsTools.collidableCheck(blockState);
    }
}
