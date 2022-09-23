package dev.lazurite.rayon.impl.event;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import dev.lazurite.rayon.BukkitNmsUtil;
import dev.lazurite.rayon.api.event.physicsSpace.PhysicsSpaceElementAddedEvent;
import dev.lazurite.rayon.api.event.physicsSpace.PhysicsSpaceStepEvent;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.PressureGenerator;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.TerrainGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlockState;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;

public class RayonBukkitListener implements Listener {
    @EventHandler
    public void onServerTickStart(ServerTickStartEvent event){
        for (World world : Bukkit.getWorlds()) {
            ServerLevel level = BukkitNmsUtil.nmsWorld(world);
            ServerEventHandler.onStartLevelTick(level);
            ServerEventHandler.onEntityStartLevelTick(level);
        }
    }
    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event){
        ServerEventHandler.onServerTick(MinecraftServer.getServer());
    }

//    @EventHandler
//    public void onWorldLoad(WorldLoadEvent event) {
//        ServerEvents.Lifecycle.LOAD_LEVEL.invoke(nmsWorld(event.getWorld()));
//    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ServerLevel level = BukkitNmsUtil.nmsWorld(event.getBlock().getWorld());
        BlockState blockState = ((CraftBlockState) event.getBlock().getState()).getHandle();
        BlockPos blockPos = ((CraftBlock) event.getBlock()).getPosition();
        ServerEventHandler.onBlockUpdate(level, blockState, blockPos);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        ServerEventHandler.onEntityLoad(BukkitNmsUtil.nmsEntity(event.getEntity()));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        ServerEventHandler.onStopTrackingEntity(BukkitNmsUtil.nmsEntity(event.getEntity()));
    }

    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            ServerEventHandler.onEntityLoad(BukkitNmsUtil.nmsEntity(entity));
        }
    }

    @EventHandler
    public void onEntitiesUnload(EntitiesUnloadEvent event) {
        for (Entity entity : event.getEntities()) {
            ServerEventHandler.onStopTrackingEntity(BukkitNmsUtil.nmsEntity(entity));
        }
    }

    @EventHandler
    public void onPhysicsSpaceStep(PhysicsSpaceStepEvent event) {
        PressureGenerator.step(event.getSpace());
        TerrainGenerator.step(event.getSpace());
    }

    @EventHandler
    public void onPhysicsSpaceElementAdded(PhysicsSpaceElementAddedEvent event) {
        ServerEventHandler.onElementAddedToSpace(event.getSpace(), event.getRigidBody());
    }

}
