package dev.lazurite.rayon;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.collision.body.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("rayon")
public class RayonCommand extends BaseCommand {
    @Subcommand("test")
    public void test(Player player, @Default("1") int count) {
        for (int i = 0; i < count; i++) {
            MinecraftSpace space = MinecraftSpace.get(player.getWorld());
            float size = 5.25f;
            MinecraftShape.Convex convex = MinecraftShape.convex(new BoundingBox(new Vector3f(), size, size, size));
            //        MiniSkullModel miniSkullModel = new MiniSkullModel(MelonPacketLibApi.createGroup(location, AllPlayersGroup.INSTANCE), location, Rotation.IDENTITY);

            PhysicsRigidBody rigidBody = new PhysicsRigidBody(convex);
            PhysicsElement physicsElement = new PhysicsElement(rigidBody, space) {
                @Override
                public void updateFrame() {
                    super.updateFrame();
                    HitboxRender.render(this, player.getWorld());
                }
            };
            RigidBodyDataStorage.add(rigidBody, physicsElement);

            Location location = player.getLocation();
            rigidBody.setPhysicsLocation(new Vector3f((float) location.getX(), (float) location.getY(), (float) location.getZ()));
            MinecraftSpace.get(player.getWorld()).addCollisionObject(rigidBody);
        }
    }
}
