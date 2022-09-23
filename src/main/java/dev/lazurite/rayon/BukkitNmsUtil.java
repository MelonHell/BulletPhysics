package dev.lazurite.rayon;

import com.jme3.math.Vector3f;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class BukkitNmsUtil {
    public static net.minecraft.world.entity.Entity nmsEntity(Entity entity) {
        return ((CraftEntity) entity).getHandle();
    }
    public static ServerLevel nmsWorld(World world) {
        return ((CraftWorld) world).getHandle();
    }

    public static Vector3f vector3fFromLocation(Location location) {
        return new Vector3f((float) location.getX(), (float) location.getY(), (float) location.getZ());
    }
}
