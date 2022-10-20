package ru.melonhell.bulletphysics.bullet.collision.space;

import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.stereotype.Component;
import ru.melonhell.bulletphysics.bullet.collision.space.generator.PressureGenerator;
import ru.melonhell.bulletphysics.bullet.collision.space.generator.TerrainGenerator;
import ru.melonhell.bulletphysics.bullet.thread.PhysicsThread;
import ru.melonhell.bulletphysics.nms.NmsTools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SpaceService {
    private final JavaPlugin javaPlugin;
    private final PhysicsThread physicsThread;
    private final NmsTools nmsTools;
    private final TerrainGenerator terrainGenerator;
    private final PressureGenerator pressureGenerator;
    private final Map<World, MinecraftSpace> SPACE_MAP = new HashMap<>();

    public MinecraftSpace get(World world) {
        MinecraftSpace minecraftSpace = SPACE_MAP.get(world);
        if (minecraftSpace == null) {
            final var space = new MinecraftSpace(javaPlugin, physicsThread, nmsTools, terrainGenerator, pressureGenerator, world);
            set(world, space);
            return space;
        }
        return minecraftSpace;
    }

    public Collection<MinecraftSpace> getAll() {
        return SPACE_MAP.values();
    }

    public void set(World level, MinecraftSpace minecraftSpace) {
        SPACE_MAP.put(level, minecraftSpace);
    }
}
