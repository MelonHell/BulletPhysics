package dev.lazurite.rayon;

import dev.lazurite.rayon.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.event.ServerEventHandler;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class SpaceStorage {
    private static final Map<Level, MinecraftSpace> SPACE_MAP = new HashMap<>();

    public static MinecraftSpace get(Level level) {
        MinecraftSpace minecraftSpace = SPACE_MAP.get(level);
        if (minecraftSpace == null) {
            final var space = new MinecraftSpace(ServerEventHandler.getThread(), level);
            set(level, space);
            PhysicsSpaceEvents.INIT.invoke(space);
            return space;
        }
        return minecraftSpace;
    }

    public static void set(Level level, MinecraftSpace minecraftSpace) {
        SPACE_MAP.put(level, minecraftSpace);
    }
}
