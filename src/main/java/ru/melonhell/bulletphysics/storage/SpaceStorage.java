package ru.melonhell.bulletphysics.storage;

import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.springframework.stereotype.Component;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.generator.PressureGenerator;
import ru.melonhell.bulletphysics.bullet.collision.space.generator.TerrainGenerator;
import ru.melonhell.bulletphysics.bullet.thread.PhysicsThread;
import ru.melonhell.bulletphysics.nms.NmsTools;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SpaceStorage {
    private final PhysicsThread physicsThread;
    private final NmsTools nmsTools;
    private final TerrainGenerator terrainGenerator;
    private final PressureGenerator pressureGenerator;
    private final Map<World, MinecraftSpace> SPACE_MAP = new HashMap<>();

    public MinecraftSpace get(World world) {
        MinecraftSpace minecraftSpace = SPACE_MAP.get(world);
        if (minecraftSpace == null) {
            final var space = new MinecraftSpace(physicsThread, this, nmsTools, terrainGenerator, pressureGenerator, world);
            set(world, space);
//            Bukkit.getPluginManager().callEvent(new PhysicsSpaceInitEvent(space));
            return space;
        }
        return minecraftSpace;
    }

    public void set(World level, MinecraftSpace minecraftSpace) {
        SPACE_MAP.put(level, minecraftSpace);
    }
}
