package ru.melonhell.bulletphysics.bullet.collision.space.cache;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import org.bukkit.block.BlockState;
import ru.melonhell.bulletphysics.bullet.collision.body.shape.MinecraftShape;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.block.BlockProperty;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.data.BlockData;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.data.FluidColumn;
import ru.melonhell.bulletphysics.nms.NmsTools;
import ru.melonhell.bulletphysics.nms.wrappers.AABBWrapper;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPosWrapper;
import ru.melonhell.bulletphysics.utils.math.Convert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleChunkCache implements ChunkCache {
    private final MinecraftSpace space;
    private final Map<BlockPosWrapper, BlockData> blockData;
    private final List<FluidColumn> fluidColumns;
    private final List<BlockPosWrapper> activePositions;

    private final NmsTools nmsTools;

    public SimpleChunkCache(MinecraftSpace space, NmsTools nmsTools) {
        this.space = space;
        this.nmsTools = nmsTools;
        this.blockData = new ConcurrentHashMap<>();
        this.fluidColumns = new ArrayList<>();
        this.activePositions = new ArrayList<>();
    }

    @Override
    public void loadFluidData(BlockPosWrapper blockPos) {
        final var world = space.getWorld();

        if (!nmsTools.getFluidState(blockPos.toBlock(world)).isEmpty()) {
            if (this.fluidColumns.stream().noneMatch(column -> column.contains(blockPos))) {
                this.fluidColumns.add(new FluidColumn(blockPos.toBlock(world), nmsTools));
            }
        }
    }

    @Override
    public void loadBlockData(BlockPosWrapper blockPos) {
        this.blockData.remove(blockPos);

        final var world = space.getWorld();
        final var block = blockPos.toBlock(world);
        final var blockState = block.getState();

        if (isValidBlock(blockState)) {
            final var properties = BlockProperty.getBlockProperty(blockState.getType());

//            if (!blockState.isCollisionShapeFullBlock(level, blockPos) || (properties != null && !properties.isFullBlock())) {
//                Pattern pattern;
//
//                if (space.isServer()) {
//                    pattern = Transporter.getPatternBuffer().getBlock(Block.getId(blockState));
//                } else {
//                    pattern = ChunkCache.genShapeForBlock(level, blockPos, blockState);
//                }
//
//                if (pattern != null && !pattern.getQuads().isEmpty()) {
//                    shape = MinecraftShape.concave(pattern);
//                }
//            }

            // TODO ступеньки хуеньки итд

//            final var voxelShape = blockState.getCollisionShape(level, blockPos);
//            final var boundingBox = voxelShape.isEmpty() ? new AABBWrapper(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f) : voxelShape.bounds();
            AABBWrapper boundingBox = nmsTools.boundingBox(block);
            MinecraftShape.Convex shape = MinecraftShape.convex(boundingBox);


            this.blockData.put(blockPos, new BlockData(block, blockState, shape));
        }
    }

    @Override
    public void refreshAll() {
        final var world = space.getWorld();
        this.activePositions.clear();

        for (var elementRigidBodyData : space.getElementRigidBodyDataList()) {
            PhysicsRigidBody rigidBody = elementRigidBodyData.getRigidBody();
            if (!elementRigidBodyData.isTerrainLoadingEnabled() || !rigidBody.isActive()) {
                continue;
            }

            final var aabb = Convert.toMinecraft(rigidBody.boundingBox(new BoundingBox())).inflate(1.0f);

            nmsTools.betweenClosedStream(aabb).forEach(blockPos -> {
                this.activePositions.add(blockPos);

                this.getBlockData(blockPos).ifPresentOrElse(blockData -> {
                    final var blockState = blockPos.toBlock(world).getState();


                    if (nmsTools.getBlockId(blockData.blockState()) != nmsTools.getBlockId(blockState)) {
                        loadBlockData(blockPos);
                    }
                }, () -> loadBlockData(blockPos));

                if (this.getFluidColumn(blockPos).isEmpty()) {
                    loadFluidData(blockPos);
                }
            });
        }

        this.blockData.keySet().removeIf(blockPos -> !this.activePositions.contains(blockPos));
        this.fluidColumns.removeIf(column -> this.activePositions.stream().noneMatch(column::contains));
    }

    @Override
    public MinecraftSpace getSpace() {
        return this.space;
    }

    @Override
    public List<BlockData> getBlockData() {
        return new ArrayList<>(this.blockData.values());
    }

    @Override
    public List<FluidColumn> getFluidColumns() {
        return new ArrayList<>(this.fluidColumns);
    }

    @Override
    public Optional<BlockData> getBlockData(BlockPosWrapper blockPos) {
        return Optional.ofNullable(this.blockData.get(blockPos));
    }

    @Override
    public Optional<FluidColumn> getFluidColumn(BlockPosWrapper blockPos) {
        for (var column : getFluidColumns()) {
            if (column.contains(blockPos)) {
                return Optional.of(column);
            }
        }

        return Optional.empty();
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