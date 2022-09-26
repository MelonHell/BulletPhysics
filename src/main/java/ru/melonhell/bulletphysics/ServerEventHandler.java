package ru.melonhell.bulletphysics;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.generator.EntityCollisionGenerator;
import ru.melonhell.bulletphysics.bullet.thread.PhysicsThread;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPosWrapper;
import ru.melonhell.bulletphysics.storage.SpaceStorage;
import ru.spliterash.springspigot.listener.SpigotListener;

@SpigotListener
@RequiredArgsConstructor
public class ServerEventHandler implements Listener {
    private final SpaceStorage spaceStorage;
    private final PhysicsThread physicsThread;

    @EventHandler
    public void onServerTickStart(ServerTickStartEvent event) {
        for (World world : Bukkit.getWorlds()) {
            MinecraftSpace space = spaceStorage.get(world);
            space.step();
            EntityCollisionGenerator.step(space);
        }
    }

    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event) {
        if (physicsThread.throwable != null) {
            throw new RuntimeException(physicsThread.throwable);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        spaceStorage.get(event.getBlock().getWorld()).doBlockUpdate(new BlockPosWrapper(event.getBlock()));
    }
}
