package ru.melonhell.bulletphysics.bullet.collision.space.generator;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.melonhell.bulletphysics.bullet.collision.body.TerrainRigidBody;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.nms.NmsTools;
import ru.melonhell.bulletphysics.utils.math.BoundingBoxUtils;

import java.util.HashSet;

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
        final var chunkCache = space.getChunkCache();
        final var keep = new HashSet<TerrainRigidBody>();

        for (var elementRigidBodyData : space.getElementRigidBodyDataList()) {
            PhysicsRigidBody rigidBody = elementRigidBodyData.getRigidBody();
            if (!elementRigidBodyData.isTerrainLoadingEnabled() || !rigidBody.isActive()) {
                continue;
            }

            BoundingBox box = rigidBody.boundingBox(new BoundingBox());
            BoundingBoxUtils.inflate(box, 1.0f);

            nmsTools.betweenClosedStream(box).forEach(blockPos -> {
                chunkCache.getBlockData(blockPos).ifPresent(blockData -> {
                    space.getTerrainObjectAt(blockPos).ifPresentOrElse(terrain -> {
                        if (nmsTools.getBlockId(blockData.blockState()) != nmsTools.getBlockId(terrain.getBlockState())) {
                            space.removeCollisionObject(terrain);

                            final var terrain2 = TerrainRigidBody.from(blockData);
                            space.addCollisionObject(terrain2);
                            keep.add(terrain2);
                        } else {
                            keep.add(terrain);
                        }
                    }, () -> {
                        final var terrain = TerrainRigidBody.from(blockData);
                        space.addCollisionObject(terrain);
                        keep.add(terrain);
                    });
                });
            });
        }

        space.getTerrainMap().forEach((blockPos, terrain) -> {
            if (!keep.contains(terrain)) {
                space.removeTerrainObjectAt(blockPos);
            }
        });
    }
}