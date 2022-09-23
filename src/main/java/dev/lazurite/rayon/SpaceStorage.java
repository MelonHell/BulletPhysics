package dev.lazurite.rayon;

import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class SpaceStorage {
    private static final Map<World, MinecraftSpace> SPACE_MAP = new HashMap<>();

    public static MinecraftSpace get(World world) {
        MinecraftSpace minecraftSpace = SPACE_MAP.get(world);
        if (minecraftSpace == null) {
            final var space = new MinecraftSpace(RayonPlugin.getThread(), world);
            set(world, space);
//            Bukkit.getPluginManager().callEvent(new PhysicsSpaceInitEvent(space));
            return space;
        }
        return minecraftSpace;
    }

    public static void set(World level, MinecraftSpace minecraftSpace) {
        SPACE_MAP.put(level, minecraftSpace);
    }
}
