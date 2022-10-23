package ru.melonhell.bulletphysics.bullet.collision.space.cache;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import org.bukkit.block.BlockState;
import ru.melonhell.bulletphysics.bullet.collision.body.element.PhysicsElement;
import ru.melonhell.bulletphysics.bullet.collision.body.shape.MinecraftShape;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.block.BlockProperty;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.data.FluidColumn;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.data.PhysicsBlockData;
import ru.melonhell.bulletphysics.nms.NmsTools;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos;
import ru.melonhell.bulletphysics.utils.math.BetweenClosedUtils;
import ru.melonhell.bulletphysics.utils.math.BoundingBoxUtils;

import java.util.*;

public class SimpleBlockCache implements BlockCache {
    private final boolean AUTO_REFRESH = false;

    private final MinecraftSpace space;
    private final NmsTools nmsTools;
    private final Map<BlockPos, PhysicsBlockData> physicsBlockDataMap;
    private final List<FluidColumn> fluidColumns;
    private final Set<BlockPos> activePositions;

    public SimpleBlockCache(MinecraftSpace space, NmsTools nmsTools) {
        this.space = space;
        this.nmsTools = nmsTools;
        this.physicsBlockDataMap = new HashMap<>();
        this.fluidColumns = new ArrayList<>();
        this.activePositions = new HashSet<>(128);
    }

    @Override
    public void loadFluidData(BlockPos blockPos) {
        final var world = space.getWorld();

        if (!nmsTools.getFluidState(blockPos.getBlock(world)).isEmpty()) {
            if (this.fluidColumns.stream().noneMatch(column -> column.contains(blockPos))) {
                this.fluidColumns.add(new FluidColumn(blockPos.getBlock(world), nmsTools));
            }
        }
    }

    @Override
    public void loadBlockData(BlockPos blockPos, BlockState blockState) {
        this.physicsBlockDataMap.remove(blockPos);
        final var world = space.getWorld();
        final var block = blockPos.getBlock(world);
        MinecraftShape shape = null;
        if (isValidBlock(blockState)) {
            List<BoundingBox> boundingBoxes = nmsTools.boundingBoxes(blockPos, world, blockState);
            if (!boundingBoxes.isEmpty()) {
                shape = MinecraftShape.convex(boundingBoxes);
            }
        }
        this.physicsBlockDataMap.put(blockPos, new PhysicsBlockData(block, blockState, shape));

    }

    @Override
    public void refreshAll() {
        activePositions.clear();

        for (PhysicsElement elementRigidBodyData : space.getPhysicsElements()) {
            PhysicsRigidBody rigidBody = elementRigidBodyData.getRigidBody();
            if (!elementRigidBodyData.isTerrainLoadingEnabled() || !rigidBody.isActive()) continue;
            BoundingBox box = rigidBody.boundingBox(new BoundingBox());
            BoundingBoxUtils.inflate(box, 1f);
            BetweenClosedUtils.betweenClosed(box, activePositions);
        }

        for (BlockPos blockPos : activePositions) {
            final var physicsBlockData = this.getBlockData(blockPos);
            if (physicsBlockData != null) {
                if (AUTO_REFRESH) {
                    final var blockState = blockPos.getBlockState(space.getWorld());
                    if (!nmsTools.equalsById(physicsBlockData.blockState(), blockState)) {
                        loadBlockData(blockPos, blockState);
                    }
                }
            } else {
                loadBlockData(blockPos, blockPos.getBlockState(space.getWorld()));
            }

            if (this.getFluidColumn(blockPos) != null) {
                loadFluidData(blockPos);
            }
        }

        this.physicsBlockDataMap.keySet().removeIf(blockPos -> !activePositions.contains(blockPos));
        this.fluidColumns.removeIf(column -> activePositions.stream().noneMatch(column::contains));
    }

    @Override
    public MinecraftSpace getSpace() {
        return this.space;
    }

    @Override
    public List<PhysicsBlockData> getPhysicsBlockDataMap() {
        return new ArrayList<>(this.physicsBlockDataMap.values());
    }

    @Override
    public List<FluidColumn> getFluidColumns() {
        return new ArrayList<>(this.fluidColumns);
    }

    @Override
    public PhysicsBlockData getBlockData(BlockPos blockPos) {
        return this.physicsBlockDataMap.get(blockPos);
    }

    @Override
    public FluidColumn getFluidColumn(BlockPos blockPos) {
        for (var column : getFluidColumns()) {
            if (column.contains(blockPos)) {
                return column;
            }
        }
        return null;
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