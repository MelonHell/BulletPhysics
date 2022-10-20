package ru.melonhell.bulletphysics.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.melonhell.bulletphysics.init.BulletPhysicsPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@UtilityClass
public class SchedulerUtils {
    private final JavaPlugin javaPlugin = JavaPlugin.getPlugin(BulletPhysicsPlugin.class);

    public void runSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask(javaPlugin, runnable);
        }
    }

    @SneakyThrows
    public <T> CompletableFuture<T> runSyncFuture(Supplier<T> supplier) {
        CompletableFuture<T> future = new CompletableFuture<>();
        if (Bukkit.isPrimaryThread()) {
            future.complete(supplier.get());
        } else {
            Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(BulletPhysicsPlugin.class), () -> future.complete(supplier.get()));
        }
        return future;
    }
}
