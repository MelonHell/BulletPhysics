package dev.lazurite.rayon;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import dev.lazurite.rayon.toolbox.api.event.ServerEvents;
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
            ServerEvents.Tick.START_LEVEL_TICK.invoke(BukkitNmsUtil.nmsWorld(world));
        }
    }
    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event){
        ServerEvents.Tick.END_SERVER_TICK.invoke(MinecraftServer.getServer());
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
        ServerEvents.Block.BLOCK_UPDATE.invoke(level, blockState, blockPos);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        ServerEvents.Entity.LOAD.invoke(BukkitNmsUtil.nmsEntity(event.getEntity()));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        ServerEvents.Entity.UNLOAD.invoke(BukkitNmsUtil.nmsEntity(event.getEntity()));
    }

    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            ServerEvents.Entity.LOAD.invoke(BukkitNmsUtil.nmsEntity(entity));
        }
    }

    @EventHandler
    public void onEntitiesUnload(EntitiesUnloadEvent event) {
        for (Entity entity : event.getEntities()) {
            ServerEvents.Entity.UNLOAD.invoke(BukkitNmsUtil.nmsEntity(entity));
        }
    }



}
