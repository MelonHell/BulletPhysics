package ru.melonhell.bulletphysics;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import lombok.experimental.UtilityClass;
import org.bukkit.*;
import ru.melonhell.bulletphysics.bullet.collision.body.element.PhysicsElement;
import ru.melonhell.bulletphysics.bullet.collision.body.shape.Triangle;
import ru.melonhell.bulletphysics.init.BulletPhysicsPlugin;
import ru.melonhell.bulletphysics.utils.math.MathShit;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class HitboxRender {

    public void render(PhysicsElement physicsElement, World world) {
        Bukkit.getScheduler().runTaskAsynchronously(BulletPhysicsPlugin.getPlugin(BulletPhysicsPlugin.class), () -> renderSync(physicsElement, world));
    }
    public void renderSync(PhysicsElement physicsElement, World world) {
        PhysicsRigidBody rigidBody = physicsElement.getRigidBody();
        Vector3f position = rigidBody.getMotionState().getLocation(new Vector3f());
        Quaternion orientation = rigidBody.getMotionState().getOrientation(new Quaternion());
        List<Triangle> triangles = physicsElement.getMinecraftShape().getTriangles(Quaternion.IDENTITY);
        for (Triangle triangle : triangles) {
            triangle(triangle, world, orientation, position, false);
        }
    }

    public void triangle(Triangle triangle, World world, Quaternion orientation, Vector3f position, boolean lines) {
        List<Vector3f> vertices = Arrays.stream(triangle.transform(orientation).getVertices()).map(vector3f -> vector3f.add(position)).toList();
        for (int i = 0; i < 3; i++) {
            Vector3f v1 = vertices.get(i);
            Vector3f v2 = vertices.get((i + 1) % 3);
            if (lines) line(v1, v2, 3, world);
            particle(new Location(world, v1.x, v1.y, v1.z), Color.RED);
        }
    }

    public void line(Vector3f v1, Vector3f v2, int count, World world) {
        for (int i = 1; i < count; i++) {
            Vector3f lerp = MathShit.lerp(v1, v2, (float) i / count);
            particle(new Location(world, lerp.x, lerp.y, lerp.z), Color.YELLOW);
        }
    }

    public void particle(Location location, Color color) {
        location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, new Particle.DustOptions(color, 1));
    }
}
