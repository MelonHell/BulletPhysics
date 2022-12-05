package ru.melonhell.bulletphysics.utils

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import ru.melonhell.bulletphysics.init.BulletPhysicsPlugin
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

object SchedulerUtils {
    private val javaPlugin: JavaPlugin = JavaPlugin.getPlugin(
        BulletPhysicsPlugin::class.java
    )

    fun runSync(runnable: Runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run()
        } else {
            Bukkit.getScheduler().runTask(javaPlugin, runnable)
        }
    }

    fun <T> runSyncFuture(supplier: Supplier<T>): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        if (Bukkit.isPrimaryThread()) {
            future.complete(supplier.get())
        } else {
            Bukkit.getScheduler().runTask(
                JavaPlugin.getPlugin(
                    BulletPhysicsPlugin::class.java
                ), Runnable { future.complete(supplier.get()) })
        }
        return future
    }
}