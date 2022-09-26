package ru.melonhell.bulletphysics;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import ru.melonhell.bulletphysics.bullet.collision.body.element.PhysicsElement;
import ru.melonhell.bulletphysics.bullet.collision.body.shape.Triangle;

import java.util.List;

public class HitboxRender {
    public static void render(PhysicsElement physicsElement, World world) {
        PhysicsRigidBody rigidBody = physicsElement.getRigidBody();
        List<Triangle> triangles = physicsElement.getMinecraftShape().getTriangles(Quaternion.IDENTITY);
        for (Triangle triangle : triangles) {
            for (Vector3f vertex : triangle.transform(rigidBody.getMotionState().getOrientation(new Quaternion())).getVertices()) {
                Vector3f vector3f = vertex.add(rigidBody.getMotionState().getLocation(new Vector3f()));
                Location location = new Location(world, vector3f.x, vector3f.y, vector3f.z);
                world.spawnParticle(Particle.WATER_BUBBLE, location, 1);
            }
        }
    }
}
