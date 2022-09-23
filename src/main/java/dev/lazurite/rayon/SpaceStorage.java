package dev.lazurite.rayon;

import dev.lazurite.rayon.api.event.physicsSpace.PhysicsSpaceInitEvent;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class SpaceStorage {
    private static final Map<Level, MinecraftSpace> SPACE_MAP = new HashMap<>();

    public static MinecraftSpace get(Level level) {
        MinecraftSpace minecraftSpace = SPACE_MAP.get(level);
        if (minecraftSpace == null) {
            final var space = new MinecraftSpace(RayonPlugin.getThread(), level);
            set(level, space);
            Bukkit.getPluginManager().callEvent(new PhysicsSpaceInitEvent(space));
            return space;
        }
        return minecraftSpace;
    }

    public static void set(Level level, MinecraftSpace minecraftSpace) {
        SPACE_MAP.put(level, minecraftSpace);
    }
}
