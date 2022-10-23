package ru.melonhell.bulletphysics.nms.wrappers;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class BlockPos {
    private final int x;
    private final int y;
    private final int z;

    public BlockPos(Block block) {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
    }

    public Block getBlock(World world) {
        return world.getBlockAt(x, y, z);
    }

    public BlockState getBlockState(World world) {
        return world.getBlockState(x, y, z);
    }

    public BoundingBox boundingBox() {
        return new BoundingBox(new Vector3f(x, y, z), new Vector3f(x + 1, y + 1, z + 1));
    }
}
