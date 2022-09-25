package dev.lazurite.rayon;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import ru.melonhell.animatronics.math.Rotation;
import ru.melonhell.animatronics.models.simple.MiniSkullModel;
import ru.melonhell.melonpacketlib.MelonPacketLibApi;
import ru.melonhell.melonpacketlib.playerGroup.buildIn.AllPlayersGroup;


public class TestPhysicsElement implements PhysicsElement {
    private final ElementRigidBody rigidBody;
    private final MinecraftShape.Convex convex;
    private final World world;

    public TestPhysicsElement(Location location) {
        this.world = location.getWorld();
        MinecraftSpace space = MinecraftSpace.get(location.getWorld());
        float size = 0.20f;
        this.convex = MinecraftShape.convex(new BoundingBox(new Vector3f(), size, size, size));
        MiniSkullModel miniSkullModel = new MiniSkullModel(MelonPacketLibApi.createGroup(location, AllPlayersGroup.INSTANCE), location, Rotation.IDENTITY);

        this.rigidBody = new ElementRigidBody(this, space, convex, 10.0f, 0.25f, 1.0f, 0.5f) {
            @Override
            public void updateFrame() {
                super.updateFrame();
                Vector3f physicsLocation = getMotionState().getLocation(null);
                Quaternion orientation = getMotionState().getOrientation((Quaternion) null);
                Location testLoc = new Location(world, physicsLocation.x, physicsLocation.y, physicsLocation.z);
                Rotation rotation = new Rotation(orientation.getZ(), orientation.getY(), orientation.getX(), orientation.getW(), false);
                miniSkullModel.teleport(testLoc, rotation);
                world.spawnParticle(Particle.WATER_BUBBLE, testLoc, 1);
            }
        };
        this.rigidBody.setPhysicsLocation(new Vector3f((float) location.getX(), (float) location.getY(), (float) location.getZ()));
    }

    @Override
    public ElementRigidBody getRigidBody() {
        return rigidBody;
    }

    @Override
    public MinecraftShape.Convex createShape() {
        return convex;
    }
}
