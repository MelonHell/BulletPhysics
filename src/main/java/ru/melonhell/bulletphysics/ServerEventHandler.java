package ru.melonhell.bulletphysics;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.generator.EntityCollisionGenerator;
import ru.melonhell.bulletphysics.bullet.thread.PhysicsThread;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPosWrapper;
import ru.melonhell.bulletphysics.storage.SpaceStorage;
import ru.spliterash.springspigot.listener.SpigotListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@SpigotListener
@RequiredArgsConstructor
public class ServerEventHandler implements Listener {
    private final JavaPlugin javaPlugin;
    private final SpaceStorage spaceStorage;
    private final PhysicsThread physicsThread;
    private BukkitTask bukkitTask;

    @PostConstruct
    private void init() {
        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(javaPlugin, () -> {
            for (World world : Bukkit.getWorlds()) {
                MinecraftSpace space = spaceStorage.get(world);
                space.step();
                EntityCollisionGenerator.step(space);
            }
            if (physicsThread.throwable != null) {
                throw new RuntimeException(physicsThread.throwable);
            }
        }, 0, 1);
    }

    @PreDestroy
    private void destroy() {
        bukkitTask.cancel();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        spaceStorage.get(event.getBlock().getWorld()).doBlockUpdate(new BlockPosWrapper(event.getBlock()));
    }
}
