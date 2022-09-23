package dev.lazurite.rayon.impl.event;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.BukkitNmsUtil;
import dev.lazurite.rayon.RayonPlugin;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.api.event.physicsSpace.PhysicsSpaceElementAddedEvent;
import dev.lazurite.rayon.api.event.physicsSpace.PhysicsSpaceStepEvent;
import dev.lazurite.rayon.impl.bullet.collision.body.EntityRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.EntityCollisionGenerator;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.PressureGenerator;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.TerrainGenerator;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock;
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
            ServerLevel level = BukkitNmsUtil.nmsWorld(world);
            MinecraftSpace space = MinecraftSpace.get(level);
            space.step();
            EntityCollisionGenerator.step(space);
            for (var rigidBody : space.getRigidBodiesByClass(EntityRigidBody.class)) {
                /* Set entity position */
                final var location = rigidBody.getFrame().getLocation(new Vector3f(), 1.0f);
                rigidBody.getElement().cast().absMoveTo(location.x, location.y, location.z);
            }
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
        ServerLevel level = BukkitNmsUtil.nmsWorld(event.getBlock().getWorld());
        BlockPos blockPos = ((CraftBlock) event.getBlock()).getPosition();
        MinecraftSpace.getOptional(level).ifPresent(space -> space.doBlockUpdate(blockPos));
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        entityLoad(BukkitNmsUtil.nmsEntity(event.getEntity()));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        entityUnload(BukkitNmsUtil.nmsEntity(event.getEntity()));
    }

    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            entityLoad(BukkitNmsUtil.nmsEntity(entity));
        }
    }

    @EventHandler
    public void onEntitiesUnload(EntitiesUnloadEvent event) {
        for (Entity entity : event.getEntities()) {
            entityUnload(BukkitNmsUtil.nmsEntity(entity));
        }
    }

    @EventHandler
    public void onPhysicsSpaceStep(PhysicsSpaceStepEvent event) {
        PressureGenerator.step(event.getSpace());
        TerrainGenerator.step(event.getSpace());
    }

    @EventHandler
    public void onPhysicsSpaceElementAdded(PhysicsSpaceElementAddedEvent event) {
        if (event.getRigidBody() instanceof EntityRigidBody entityBody) {
            final var pos = entityBody.getElement().cast().position();
            final var box = entityBody.getElement().cast().getBoundingBox();
            entityBody.setPhysicsLocation(Convert.toBullet(pos.add(0, box.getYsize() / 2.0, 0)));
        }
    }

    private static void entityLoad(net.minecraft.world.entity.Entity entity) {
        if (entity instanceof EntityPhysicsElement element) {
            final var space = MinecraftSpace.get(entity.level);
            space.getWorkerThread().execute(() -> space.addCollisionObject(element.getRigidBody()));
        }
    }

    private static void entityUnload(net.minecraft.world.entity.Entity entity) {
        if (entity instanceof EntityPhysicsElement element) {
            final var space = MinecraftSpace.get(entity.level);
            space.getWorkerThread().execute(() -> space.removeCollisionObject(element.getRigidBody()));
        }
    }
}
