package dev.lazurite.rayon;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.Triangle;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.List;

public class HitboxRender {
    public static void render(ElementRigidBody rigidBody, World world) {
        List<Triangle> triangles = rigidBody.getMinecraftShape().getTriangles(Quaternion.IDENTITY);
        for (Triangle triangle : triangles) {
            for (Vector3f vertex : triangle.transform(rigidBody.getMotionState().getOrientation(new Quaternion())).getVertices()) {
                Vector3f vector3f = vertex.add(rigidBody.getMotionState().getLocation(new Vector3f()));
                Location location = new Location(world, vector3f.x, vector3f.y, vector3f.z);
                world.spawnParticle(Particle.WATER_BUBBLE, location, 1);
            }
        }
    }
}
