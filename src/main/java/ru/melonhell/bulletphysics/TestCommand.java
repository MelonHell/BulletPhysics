package ru.melonhell.bulletphysics;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;
import ru.melonhell.bulletphysics.bullet.collision.body.element.PhysicsElement;
import ru.melonhell.bulletphysics.bullet.collision.body.shape.MinecraftShape;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.storage.SpaceStorage;

@Component
@CommandAlias("bulletphysics")
@RequiredArgsConstructor
public class TestCommand extends BaseCommand {
    private final SpaceStorage spaceStorage;

    @Subcommand("test")
    public void test(Player player, @Default("1") int count) {
        for (int i = 0; i < count; i++) {
            MinecraftSpace space = spaceStorage.get(player.getWorld());
            float size = 2f;
            MinecraftShape.Convex convex = MinecraftShape.convex(new BoundingBox(new Vector3f(), size, size, size));

            PhysicsRigidBody rigidBody = new PhysicsRigidBody(convex);
            PhysicsElement physicsElement = new PhysicsElement(rigidBody) {
                @Override
                public void updateFrame() {
                    super.updateFrame();
                    HitboxRender.render(this, player.getWorld());
                }
            };

            Location location = player.getLocation();
            rigidBody.setPhysicsLocation(new Vector3f((float) location.getX(), (float) location.getY(), (float) location.getZ()));
            space.addPhysicsElement(physicsElement);
        }
    }
}
