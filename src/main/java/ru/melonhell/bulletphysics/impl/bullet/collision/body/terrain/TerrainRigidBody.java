package ru.melonhell.bulletphysics.impl.bullet.collision.body.terrain;

import com.jme3.math.Vector3f;
import ru.melonhell.bulletphysics.impl.bullet.collision.body.shape.MinecraftShape;
import ru.melonhell.bulletphysics.impl.bullet.collision.space.block.BlockProperty;
import ru.melonhell.bulletphysics.impl.bullet.collision.space.cache.ChunkCache;
import ru.melonhell.bulletphysics.impl.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPosWrapper;
import org.bukkit.block.BlockState;

public class TerrainRigidBody extends MinecraftRigidBody {
    private final BlockPosWrapper blockPos;
    private final BlockState state;

    public static TerrainRigidBody from(ChunkCache.BlockData blockData, MinecraftSpace space) {
        final var blockProperty = BlockProperty.getBlockProperty(blockData.blockState().getType());
        final var friction = blockProperty == null ? 0.75f : blockProperty.friction();
        final var restitution = blockProperty == null ? 0.25f : blockProperty.restitution();
        return new TerrainRigidBody(space, blockData.shape(), new BlockPosWrapper(blockData.block()), blockData.blockState(), friction, restitution);
    }

    public TerrainRigidBody(MinecraftSpace space, MinecraftShape shape, BlockPosWrapper blockPos, BlockState blockState, float friction, float restitution) {
        super(space, shape);
        this.blockPos = blockPos;
        this.state = blockState;

        this.setFriction(friction);
        this.setRestitution(restitution);
        this.setPhysicsLocation(new Vector3f(blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f));
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