package ru.melonhell.bulletphysics.bullet.collision.space.block

import org.bukkit.Material
import java.util.concurrent.ConcurrentHashMap

data class BlockProperty(
    val friction: Float,
    val restitution: Float,
    val collidable: Boolean,
    val fullBlock: Boolean
) {
    companion object {
        private val blockProperties: MutableMap<Material, BlockProperty> = ConcurrentHashMap()

        init {
            addBlockProperty(Material.ICE, 0.05f, 0.25f, true, true)
            addBlockProperty(Material.SLIME_BLOCK, 3.0f, 3.0f, true, true)
            addBlockProperty(Material.HONEY_BLOCK, 3.0f, 0.25f, true, true)
            addBlockProperty(Material.SOUL_SAND, 3.0f, 0.25f, true, true)
            addBlockProperty(Material.LECTERN, 0.75f, 0.25f, true, false)
            addBlockProperty(Material.SNOW, 1.0f, 0.15f, true, true)
        }

        private fun addBlockProperty(
            block: Material,
            friction: Float,
            restitution: Float,
            collidable: Boolean,
            fullBlock: Boolean
        ) {
            blockProperties[block] = BlockProperty(
                friction.coerceAtLeast(0.0f),
                restitution.coerceAtLeast(0.0f),
                collidable,
                fullBlock
            )
        }

        @JvmStatic
        fun getBlockProperty(block: Material): BlockProperty? {
            return blockProperties[block]
        }
    }
}