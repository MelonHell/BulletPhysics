package dev.lazurite.rayon;

import co.aikar.commands.PaperCommandManager;
import dev.lazurite.rayon.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.impl.event.ServerEventHandler;
import dev.lazurite.rayon.nms.NmsTools;
import dev.lazurite.rayon.nms.NmsTools_v1_19_2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class RayonPlugin extends JavaPlugin {
    public static final Logger LOGGER = LogManager.getLogger("Rayon");
    private static PhysicsThread thread;

    private static NmsTools nmsTools;

    public static PhysicsThread getThread() {
        return thread;
    }

    public static NmsTools getNmsTools() {
        return nmsTools;
    }

    @Override
    public void onEnable() {
        nmsTools = new NmsTools_v1_19_2();
        File libFile = new File(getDataFolder(), NativeLibraryLoader.getName("Release", "Sp"));
        libFile = NativeLibraryLoader.copyTmp(libFile);
        if (!NativeLibraryLoader.load(libFile)) {
            throw new RuntimeException("Lib is not loaded");
        }

        thread = new PhysicsThread(Thread.currentThread(), "Server Physics Thread");
        Bukkit.getPluginManager().registerEvents(new ServerEventHandler(), this);
        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new RayonCommand());
    }

    @Override
    public void onDisable() {
        thread.destroy();
    }
}
