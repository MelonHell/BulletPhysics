package dev.lazurite.rayon.impl.event;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.RayonPlugin;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.EntityCollisionGenerator;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.PressureGenerator;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.TerrainGenerator;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.nms.wrappers.BlockPosWrapper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;

public class ServerEventHandler implements Listener {
    @EventHandler
    public void onServerTickStart(ServerTickStartEvent event){
        for (World world : Bukkit.getWorlds()) {
            MinecraftSpace space = MinecraftSpace.get(world);
            space.step();
            EntityCollisionGenerator.step(space);
//            for (var rigidBody : space.getRigidBodiesByClass(EntityRigidBody.class)) {
//                /* Set entity position */
//                final var location = rigidBody.getFrame().getLocation(new Vector3f(), 1.0f);
//                rigidBody.getElement().cast().absMoveTo(location.x, location.y, location.z);
//            }
        }
    }
    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event){
        PhysicsThread thread = RayonPlugin.getThread();
        if (thread.throwable != null) {
            throw new RuntimeException(thread.throwable);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        MinecraftSpace.get(event.getBlock().getWorld()).doBlockUpdate(new BlockPosWrapper(event.getBlock()));
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        entityLoad(event.getEntity());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        entityUnload(event.getEntity());
    }

    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            entityLoad(entity);
        }
    }

    @EventHandler
    public void onEntitiesUnload(EntitiesUnloadEvent event) {
        for (Entity entity : event.getEntities()) {
            entityUnload(entity);
        }
    }

//    @EventHandler
//    public void onPhysicsSpaceStep(PhysicsSpaceStepEvent event) {
//        physicsSpaceStep(event.getSpace());
//    }
//
//    @EventHandler
//    public void onPhysicsSpaceElementAdded(PhysicsSpaceElementAddedEvent event) {
//        if (event.getRigidBody() instanceof EntityRigidBody entityBody) {
//            final var pos = entityBody.getElement().cast().position();
//            final var box = entityBody.getElement().cast().getBoundingBox();
//            entityBody.setPhysicsLocation(Convert.toBullet(pos.add(0, box.getYsize() / 2.0, 0)));
//        }
//    }

    public static void physicsSpaceStep(MinecraftSpace space) {
        PressureGenerator.step(space);
        TerrainGenerator.step(space);
    }

    public static void physicsSpaceElementAdded(ElementRigidBody rigidBody) {
//        if (rigidBody instanceof EntityRigidBody entityBody) {
//            final var pos = entityBody.getElement().cast().position();
//            final var box = entityBody.getElement().cast().getBoundingBox();
//            entityBody.setPhysicsLocation(Convert.toBullet(pos.add(0, box.getYsize() / 2.0, 0)));
//        }
    }

    private static void entityLoad(Entity entity) {
//        net.minecraft.world.entity.Entity nmsEntity = BukkitNmsUtil.nmsEntity(entity);
//        if (nmsEntity instanceof EntityPhysicsElement element) {
//            final var space = MinecraftSpace.get(entity.getWorld());
//            space.getPhysicsThread().execute(() -> space.addCollisionObject(element.getRigidBody()));
//        }
    }

    private static void entityUnload(Entity entity) {
//        net.minecraft.world.entity.Entity nmsEntity = BukkitNmsUtil.nmsEntity(entity);
//        if (nmsEntity instanceof EntityPhysicsElement element) {
//            final var space = MinecraftSpace.get(entity.getWorld());
//            space.getPhysicsThread().execute(() -> space.removeCollisionObject(element.getRigidBody()));
//        }
    }
}
