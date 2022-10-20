package ru.melonhell.bulletphysics.bullet.collision.body;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import org.bukkit.block.BlockState;
import ru.melonhell.bulletphysics.bullet.collision.body.shape.MinecraftShape;
import ru.melonhell.bulletphysics.bullet.collision.space.block.BlockProperty;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.data.BlockData;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPosWrapper;

public class TerrainRigidBody extends PhysicsRigidBody {
    private final BlockPosWrapper blockPos;
    private final BlockState state;

    public TerrainRigidBody(MinecraftShape shape, BlockPosWrapper blockPos, BlockState blockState, float friction, float restitution) {
        super((CollisionShape) shape, massForStatic);
        this.blockPos = blockPos;
        this.state = blockState;

        this.setFriction(friction);
        this.setRestitution(restitution);
        this.setPhysicsLocation(new Vector3f(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
    }

    public static TerrainRigidBody from(BlockData blockData) {
        final var blockProperty = BlockProperty.getBlockProperty(blockData.blockState().getType());
        final var friction = blockProperty == null ? 0.75f : blockProperty.friction();
        final var restitution = blockProperty == null ? 0.25f : blockProperty.restitution();
        return new TerrainRigidBody(blockData.shape(), new BlockPosWrapper(blockData.block()), blockData.blockState(), friction, restitution);
    }

    public BlockPosWrapper getBlockPos() {
        return this.blockPos;
    }

    public BlockState getBlockState() {
        return this.state;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TerrainRigidBody terrain) {
            return terrain.getBlockPos().equals(this.blockPos) && terrain.getBlockState().equals(this.state);
        }

        return false;
    }

}