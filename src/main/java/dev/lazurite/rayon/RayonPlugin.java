package dev.lazurite.rayon;

import co.aikar.commands.PaperCommandManager;
import dev.lazurite.rayon.impl.bullet.collision.space.supplier.level.ServerLevelSupplier;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.impl.event.ServerEventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class RayonPlugin extends JavaPlugin {
    public static final Logger LOGGER = LogManager.getLogger("Rayon");
    private static PhysicsThread thread;

    public static PhysicsThread getThread() {
        return thread;
    }

    @Override
    public void onEnable() {
        File libFile = new File(getDataFolder(), NativeLibraryLoader.getName("Release", "Sp"));
        libFile = NativeLibraryLoader.copyTmp(libFile);
        if (!NativeLibraryLoader.load(libFile)) {
            throw new RuntimeException("Lib is not loaded");
        }

        thread = new PhysicsThread(Thread.currentThread(), new ServerLevelSupplier(), "Server Physics Thread");
        Bukkit.getPluginManager().registerEvents(new ServerEventHandler(), this);
        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new RayonCommand());
    }

    @Override
    public void onDisable() {
        thread.destroy();
    }
}
