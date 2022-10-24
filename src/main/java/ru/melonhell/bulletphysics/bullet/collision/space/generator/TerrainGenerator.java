package ru.melonhell.bulletphysics.bullet.collision.space.generator;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.melonhell.bulletphysics.bullet.collision.body.TerrainRigidBody;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.data.PhysicsBlockData;
import ru.melonhell.bulletphysics.nms.NmsTools;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos;
import ru.melonhell.bulletphysics.utils.math.BetweenClosedUtils;
import ru.melonhell.bulletphysics.utils.math.BoundingBoxUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Used for loading blocks into the simulation so that rigid bodies can interact with them.
 *
 * @see MinecraftSpace
 */
@Component
@RequiredArgsConstructor
public class TerrainGenerator {
    private final NmsTools nmsTools;

    public void step(MinecraftSpace space) {
        final var blockCache = space.getBlockCache();
        final var keep = new HashSet<TerrainRigidBody>();
        List<BlockPos> betweenClosed = new ArrayList<>();

        for (var elementRigidBodyData : space.getPhysicsElements()) {
            PhysicsRigidBody rigidBody = elementRigidBodyData.getRigidBody();
            if (!elementRigidBodyData.isTerrainLoadingEnabled() || !rigidBody.isActive()) continue;
            BoundingBox box = rigidBody.boundingBox(new BoundingBox());
            BoundingBoxUtils.inflate(box, 3f);
            BetweenClosedUtils.betweenClosed(box, betweenClosed);
        }

        for (BlockPos blockPos : betweenClosed) {
            PhysicsBlockData physicsBlockData = blockCache.getBlockData(blockPos);
            if (physicsBlockData != null && physicsBlockData.shape() != null) {
                TerrainRigidBody terrain = space.getTerrainObjectAt(blockPos);
                if (terrain != null) {
                    if (!nmsTools.equalsById(physicsBlockData.blockState(), terrain.getBlockState())) {
                        space.removeCollisionObject(terrain);
                        terrain = TerrainRigidBody.from(physicsBlockData);
                        space.addCollisionObject(terrain);
                    }
                } else {
                    terrain = TerrainRigidBody.from(physicsBlockData);
                    space.addCollisionObject(terrain);
                }
                keep.add(terrain);
            }
        }

        space.getTerrainMap().forEach((blockPos, terrain) -> {
            if (!keep.contains(terrain)) {
                space.removeTerrainObjectAt(blockPos);
            }
        });
    }
}