package ru.melonhell.bulletphysics.nms.wrappers;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.block.Block;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Immutable
@Getter
@RequiredArgsConstructor
public class BlockPos {
    private final int x;
    private final int y;
    private final int z;

    public BlockPos(Block block) {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
    }

    public Block toBlock(World world) {
        return world.getBlockAt(x, y, z);
    }

    public BoundingBox boundingBox() {
        return new BoundingBox(new Vector3f(x, y, z), new Vector3f(x + 1, y + 1, z + 1));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockPos that = (BlockPos) o;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
