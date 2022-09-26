package ru.melonhell.bulletphysics.bullet.collision.space.cache;

import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.data.BlockData;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.data.FluidColumn;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPosWrapper;

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
}