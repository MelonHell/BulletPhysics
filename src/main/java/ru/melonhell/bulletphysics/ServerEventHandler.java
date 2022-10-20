package ru.melonhell.bulletphysics;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.SpaceService;
import ru.melonhell.bulletphysics.bullet.collision.space.generator.EntityCollisionGenerator;
import ru.melonhell.bulletphysics.bullet.thread.PhysicsThread;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPosWrapper;
import ru.spliterash.springspigot.listener.SpigotListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@SpigotListener
@RequiredArgsConstructor
public class ServerEventHandler implements Listener {
    private final JavaPlugin javaPlugin;
    private final SpaceService spaceService;
    private final PhysicsThread physicsThread;
    private BukkitTask bukkitTask;

    @PostConstruct
    private void init() {
        bukkitTask = Bukkit.getScheduler().runTaskTimer(javaPlugin, () -> {
            for (MinecraftSpace space : spaceService.getAll()) {
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
        blockUpdate(event.getBlock());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        blockUpdate(event.getBlock());
    }

    private void blockUpdate(Block block) {
        spaceService.get(block.getWorld()).doBlockUpdate(new BlockPosWrapper(block));
    }
}
