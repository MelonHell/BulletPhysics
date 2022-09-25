package dev.lazurite.rayon.nms.wrappers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.block.Block;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Immutable
@Getter
@RequiredArgsConstructor
public class BlockPosWrapper {
    private final int x;
    private final int y;
    private final int z;

    public BlockPosWrapper(Block block) {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
    }

    public BlockPosWrapper above() {
        return new BlockPosWrapper(this.getX(), this.getY() + 1, this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosWrapper above(int distance) {
        return distance == 0 ? this : new BlockPosWrapper(this.getX(), this.getY() + distance, this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosWrapper below() {
        return new BlockPosWrapper(this.getX(), this.getY() - 1, this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosWrapper below(int i) {
        return i == 0 ? this : new BlockPosWrapper(this.getX(), this.getY() - i, this.getZ()); // Paper - Optimize BlockPosition
    }

    public Block toBlock(World world) {
        return world.getBlockAt(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockPosWrapper that = (BlockPosWrapper) o;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
