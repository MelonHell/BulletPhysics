package dev.lazurite.rayon;

import co.aikar.commands.PaperCommandManager;
import dev.lazurite.rayon.impl.event.ServerEventHandler;
import dev.lazurite.rayon.toolbox.api.event.ServerEvents;
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

        ServerEventHandler.register();
        Bukkit.getPluginManager().registerEvents(new RayonBukkitListener(), this);
        ServerEvents.Lifecycle.LOAD_SERVER.invoke(MinecraftServer.getServer());
        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new RayonCommand());
    }

    @Override
    public void onDisable() {
        ServerEvents.Lifecycle.UNLOAD_SERVER.invoke(MinecraftServer.getServer());
    }
}
