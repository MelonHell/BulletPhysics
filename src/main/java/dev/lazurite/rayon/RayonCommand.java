package dev.lazurite.rayon;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import org.bukkit.entity.Player;

@CommandAlias("rayon")
public class RayonCommand extends BaseCommand {
    @Subcommand("test")
    public void test(Player player, @Default("1") int count) {
        for (int i = 0; i < count; i++) {
            TestPhysicsElement testPhysicsElement = new TestPhysicsElement(player.getLocation());
            MinecraftSpace.get(BukkitNmsUtil.nmsWorld(player.getWorld())).addCollisionObject(testPhysicsElement.getRigidBody());
        }
    }
}
