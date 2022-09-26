package dev.lazurite.rayon.impl.event;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import dev.lazurite.rayon.RayonPlugin;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.EntityCollisionGenerator;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.PressureGenerator;
import dev.lazurite.rayon.impl.bullet.collision.space.generator.TerrainGenerator;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.nms.wrappers.BlockPosWrapper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ServerEventHandler implements Listener {
    @EventHandler
    public void onServerTickStart(ServerTickStartEvent event){
        for (World world : Bukkit.getWorlds()) {
            MinecraftSpace space = MinecraftSpace.get(world);
            space.step();
            EntityCollisionGenerator.step(space);
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

    public static void physicsSpaceStep(MinecraftSpace space) {
        PressureGenerator.step(space);
        TerrainGenerator.step(space);
    }
}
