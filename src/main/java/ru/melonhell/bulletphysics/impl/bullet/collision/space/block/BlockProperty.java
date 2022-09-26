package ru.melonhell.bulletphysics.impl.bullet.collision.space.block;

import org.bukkit.Material;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record BlockProperty (float friction, float restitution, boolean collidable, boolean isFullBlock) {
    private static final Map<Material, BlockProperty> blockProperties = new ConcurrentHashMap<>();

    static {
        BlockProperty.addBlockProperty(Material.ICE, 0.05f, 0.25f, true, true);
        BlockProperty.addBlockProperty(Material.SLIME_BLOCK, 3.0f, 3.0f, true, true);
        BlockProperty.addBlockProperty(Material.HONEY_BLOCK, 3.0f, 0.25f, true, true);
        BlockProperty.addBlockProperty(Material.SOUL_SAND, 3.0f, 0.25f, true, true);
        BlockProperty.addBlockProperty(Material.LECTERN, 0.75f, 0.25f, true, false);
        BlockProperty.addBlockProperty(Material.SNOW, 1.0f, 0.15f, true, true);
    }

    public static void addBlockProperty(Material block, float friction, float restitution, boolean collidable, boolean isFullBlock) {
        blockProperties.put(block, new BlockProperty(Math.max(friction, 0.0f), Math.max(restitution, 0.0f), collidable, isFullBlock));
    }

    public static BlockProperty getBlockProperty(Material block) {
        return blockProperties.get(block);
    }
}