package dev.lazurite.rayon.nms.wrappers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.block.Block;

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
}
