package ru.melonhell.bulletphysics.impl.bullet.collision.space.generator;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import ru.melonhell.bulletphysics.impl.bullet.collision.body.TerrainRigidBody;
import ru.melonhell.bulletphysics.impl.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.utils.NmsUtils;
import ru.melonhell.bulletphysics.utils.math.Convert;

import java.util.HashSet;

/**
 * Used for loading blocks into the simulation so that rigid bodies can interact with them.
 * @see MinecraftSpace
 */
public class TerrainGenerator {
    public static void step(MinecraftSpace space) {
        final var chunkCache = space.getChunkCache();
        final var keep = new HashSet<TerrainRigidBody>();

        for (var elementRigidBodyData : space.getElementRigidBodyDataList()) {
            PhysicsRigidBody rigidBody = elementRigidBodyData.getRigidBody();
            if (!elementRigidBodyData.isTerrainLoadingEnabled() || !rigidBody.isActive()) {
                continue;
            }

            final var aabb = Convert.toMinecraft(rigidBody.boundingBox(new BoundingBox())).inflate(0.5f);


            NmsUtils.getNmsTools().betweenClosedStream(aabb).forEach(blockPos -> {
                chunkCache.getBlockData(blockPos).ifPresent(blockData -> {
                    space.getTerrainObjectAt(blockPos).ifPresentOrElse(terrain -> {
                        if (NmsUtils.getNmsTools().getBlockId(blockData.blockState()) != NmsUtils.getNmsTools().getBlockId(terrain.getBlockState())) {
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