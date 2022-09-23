package dev.lazurite.rayon;

import co.aikar.commands.PaperCommandManager;
import dev.lazurite.rayon.impl.event.RayonBukkitListener;
import dev.lazurite.rayon.impl.event.ServerEventHandler;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class RayonPlugin extends JavaPlugin {
    public static final Logger LOGGER = LogManager.getLogger("Rayon");

    @Override
    public void onEnable() {
        File libFile = new File(getDataFolder(), NativeLibraryLoader.getName("Release", "Sp"));
        libFile = NativeLibraryLoader.copyTmp(libFile);
        if (!NativeLibraryLoader.load(libFile)) {
            throw new RuntimeException("Lib is not loaded");
        }

        Bukkit.getPluginManager().registerEvents(new RayonBukkitListener(), this);
        ServerEventHandler.onServerStart(MinecraftServer.getServer());
        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new RayonCommand());
    }

    @Override
    public void onDisable() {
        ServerEventHandler.onServerStop(MinecraftServer.getServer());
    }
}
