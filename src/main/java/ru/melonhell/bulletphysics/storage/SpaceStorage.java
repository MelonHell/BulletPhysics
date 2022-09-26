package ru.melonhell.bulletphysics.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.melonhell.bulletphysics.impl.bullet.collision.space.MinecraftSpace;
import org.bukkit.World;
import ru.melonhell.bulletphysics.impl.bullet.thread.PhysicsThread;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SpaceStorage {
    private final PhysicsThread physicsThread;
    private final RigidBodyDataStorage rigidBodyDataStorage;
    private final Map<World, MinecraftSpace> SPACE_MAP = new HashMap<>();

    public MinecraftSpace get(World world) {
        MinecraftSpace minecraftSpace = SPACE_MAP.get(world);
        if (minecraftSpace == null) {
            final var space = new MinecraftSpace(physicsThread, this, rigidBodyDataStorage, world);
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
