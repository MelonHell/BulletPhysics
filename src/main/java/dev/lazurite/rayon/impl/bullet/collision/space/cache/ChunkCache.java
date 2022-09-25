package dev.lazurite.rayon.impl.bullet.collision.space.cache;

import com.jme3.math.Vector3f;
import dev.lazurite.rayon.RayonPlugin;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.collision.space.block.BlockProperty;
import dev.lazurite.rayon.utils.math.Convert;
import dev.lazurite.rayon.nms.wrappers.BlockPosWrapper;
import dev.lazurite.rayon.nms.wrappers.FluidStateWrapper;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import java.util.List;
import java.util.Optional;

/**
 * Used for storing block that can be queried during physics execution.
 * An implementation of this should be updated/reloaded every tick on the
 * main game thread.
 *
 * @see MinecraftSpace#step
 */
public interface ChunkCache {
    static ChunkCache create(MinecraftSpace space) {
        return new SimpleChunkCache(space);
    }

    static boolean isValidBlock(BlockState blockState) {
        if (blockState == null) {
            return false;
        }

        final var block = blockState.getType();
        final var properties = BlockProperty.getBlockProperty(block);

        if (properties != null) return properties.collidable();
        return RayonPlugin.getNmsTools().collidableCheck(blockState);
    }

//    static Pattern genShapeForBlock(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
//        final var blockEntity = blockGetter.getBlockEntity(blockPos);
//        final var transformation = new PoseStack();
//        transformation.scale(0.95f, 0.95f, 0.95f);
//        transformation.translate(-0.5f, -0.5f, -0.5f);
//
//        try {
//            if (blockEntity != null) {
//                return Disassembler.getBlockEntity(blockEntity, transformation);
//            } else {
//                return Disassembler.getBlock(blockState, transformation);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    void refreshAll();

    void loadBlockData(BlockPosWrapper blockPos);

    void loadFluidData(BlockPosWrapper blockPos);

    MinecraftSpace getSpace();

    List<BlockData> getBlockData();

    List<FluidColumn> getFluidColumns();

    Optional<BlockData> getBlockData(BlockPosWrapper blockPos);

    Optional<FluidColumn> getFluidColumn(BlockPosWrapper blockPos);

    record BlockData(Block block, BlockState blockState, MinecraftShape shape) {
    }

    record FluidData(Block block, FluidStateWrapper fluidState) {
    }

    class FluidColumn {
        private final FluidData top;
        private final FluidData bottom;
        private final Vector3f flow;
        private final float height;

        public FluidColumn(Block start) {
            var cursor = start;

            var fluidState = RayonPlugin.getNmsTools().getFluidState(cursor);

            // find bottom block
            while (!fluidState.isEmpty()) {
                cursor = cursor.getRelative(BlockFace.DOWN);
                fluidState = RayonPlugin.getNmsTools().getFluidState(cursor);
            }

            cursor = cursor.getRelative(BlockFace.UP); // the above loop ends at one below the bottom
            fluidState = RayonPlugin.getNmsTools().getFluidState(cursor);
            this.bottom = new FluidData(cursor, RayonPlugin.getNmsTools().getFluidState(cursor));

            // find top block
            while (!fluidState.isEmpty()) {
                cursor = cursor.getRelative(BlockFace.UP);
                fluidState = RayonPlugin.getNmsTools().getFluidState(cursor);
            }

            cursor = cursor.getRelative(BlockFace.DOWN);
            fluidState = RayonPlugin.getNmsTools().getFluidState(cursor);

            this.top = new FluidData(cursor, fluidState);
            this.height = RayonPlugin.getNmsTools().getHeight(fluidState, cursor);

            // Water flow direction
            this.flow = Convert.toBullet(RayonPlugin.getNmsTools().getFlow(fluidState, cursor));
        }

        public boolean contains(BlockPosWrapper blockPos) {
            return top.block.getX() == blockPos.getX()
                    && top.block.getZ() == blockPos.getZ()
                    && top.block.getY() >= blockPos.getY()
                    && bottom.block.getY() <= blockPos.getY();
        }

        public FluidData getTop() {
            return this.top;
        }

        public FluidData getBottom() {
            return this.bottom;
        }

        public float getTopHeight(Vector3f position) {
//            if (flow.lengthSquared() == 0) {
//                return 0.875f;
//            }
//            final var x = position.x;
//            final var z = position.z;
//
//            final var minHeight = 0.125f;
//            final var maxHeight = height;
//
//            final var xhat = flow.dot(new Vector3f(1, 0, 0));
//            final var zhat = flow.dot(new Vector3f(0, 0, 1));
//
//            if (xhat == 0) {
//                if (zhat > 0) {
//                    return (1.0f - z) * maxHeight + z * minHeight;
//                } else if (zhat < 0) {
//                    return (1.0f - z) * minHeight + z * maxHeight;
//                }
//            } else if (zhat == 0) {
//                if (xhat > 0) {
//                    return (1.0f - x) * maxHeight + x * minHeight;
//                } else if (xhat < 0) {
//                    return (1.0f - x) * minHeight + x * maxHeight;
//                }
//            }

//            final var hitResult = topShape.clip(VectorHelper.toVec3(Convert.toMinecraft(position)), new Vec3(top.blockPos.getX() + 0.5f, top.blockPos.getY(), top.blockPos.getZ() + 0.5f), top.blockPos);
//            final var y = topShape.collide(Direction.Axis.Y, new AABB(top.blockPos).move(VectorHelper.toVec3(Convert.toMinecraft(position))), 0.875f);

//            if (hitResult != null) {
//                return position.y - (float) hitResult.getLocation().y;
//            }

            return height;
        }

        public int getHeight() {
            return this.top.block.getY() - this.bottom.block.getY() + 1;
        }

        public Vector3f getFlow() {
            return this.flow;
        }
    }
}