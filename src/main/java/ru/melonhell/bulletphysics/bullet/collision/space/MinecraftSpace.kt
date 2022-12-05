package ru.melonhell.bulletphysics.bullet.collision.space

import com.jme3.bullet.PhysicsSpace
import com.jme3.bullet.collision.PhysicsCollisionEvent
import com.jme3.bullet.collision.PhysicsCollisionListener
import com.jme3.bullet.collision.PhysicsCollisionObject
import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import org.bukkit.World
import org.bukkit.block.BlockState
import ru.melonhell.bulletphysics.bullet.collision.body.TerrainRigidBody
import ru.melonhell.bulletphysics.bullet.collision.body.element.PhysicsElement
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.BlockCache
import ru.melonhell.bulletphysics.bullet.collision.space.cache.block.BlockCacheFactory
import ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.FluidCache
import ru.melonhell.bulletphysics.bullet.collision.space.cache.fluid.FluidCacheFactory
import ru.melonhell.bulletphysics.bullet.collision.space.generator.PressureGenerator
import ru.melonhell.bulletphysics.bullet.collision.space.generator.TerrainGenerator
import ru.melonhell.bulletphysics.bullet.thread.PhysicsThread
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

/**
 * This is the main physics simulation used by Rayon. Each bullet simulation update
 * happens asynchronously while all of the setup, input, or otherwise user defined
 * behavior happens on the game logic thread.
 * <br></br><br></br>
 * It is also worth noting that another
 * simulation step will not be performed if the last step has taken longer than 50ms and is still executing upon the
 * next tick. This really only happens if you are dealing with an ungodly amount of rigid bodies or your computer is slo.
 *
 * @see PhysicsThread
 * see PhysicsSpaceEvents
 */
class MinecraftSpace(
    private val physicsThread: PhysicsThread,
    blockCacheFactory: BlockCacheFactory,
    fluidCacheFactory: FluidCacheFactory,
    private val terrainGenerator: TerrainGenerator,
    private val pressureGenerator: PressureGenerator,
    val world: World
) : PhysicsSpace(BroadphaseType.DBVT), PhysicsCollisionListener {
    private val terrainMap: MutableMap<BlockPos, TerrainRigidBody>
    private val physicsElementMap: MutableMap<PhysicsCollisionObject, PhysicsElement>
    val blockCache: BlockCache
    val fluidCache: FluidCache

    @Volatile
    var isStepping = false
        private set
    private var lastStep: Long = 0

    init {
        blockCache = blockCacheFactory.create(this)
        fluidCache = fluidCacheFactory.create(this)
        terrainMap = HashMap()
        physicsElementMap = HashMap()
        this.setGravity(Vector3f(0f, -9.8f, 0f))
        addCollisionListener(this)
        accuracy = 1.0f / 20.0f
    }

    /**
     * This method performs the following steps:
     *
     *  * Fires world step events in {link PhysicsSpaceEvents}.
     *  * Steps {link ElementRigidBody}s.
     *  * Steps the simulation asynchronously.
     *  * Triggers collision events.
     *
     *
     *
     * Additionally, none of the above steps execute when either the world is empty
     * (no [PhysicsRigidBody]s) or when the game is paused.
     *
     * @see TerrainGenerator
     * see PhysicsSpaceEvents
     */
    fun step() {
        physicsElements.forEach(Consumer { obj: PhysicsElement -> obj.updateFrame() })
        if (lastStep == 0L) {
            lastStep = System.currentTimeMillis()
            return
        }
        if (!isStepping && !isEmpty) {
            isStepping = true
            // Hop threads...
            val future = CompletableFuture.runAsync({

                /* Call collision events */
                distributeEvents()
                terrainGenerator.step(this)
                pressureGenerator.step(this)
                /* Step the Simulation */
                this.update((System.currentTimeMillis() - lastStep) / 1000.0f)
                lastStep = System.currentTimeMillis()
            }, physicsThread)
            future.whenComplete { _, ex ->
                isStepping = false
                ex?.printStackTrace()
            }
        }
    }

    fun addPhysicsElement(element: PhysicsElement) {
        addCollisionObject(element.rigidBody)
        physicsElementMap[element.rigidBody] = element
    }

    override fun addCollisionObject(collisionObject: PhysicsCollisionObject) {
        if (!collisionObject.isInWorld) {
            val physicsElement = physicsElementMap[collisionObject]
            if (physicsElement != null) {
                if (!collisionObject.isInWorld) {
                    collisionObject.activate(true)
                    physicsElement.frame[collisionObject.getPhysicsLocation(Vector3f()), collisionObject.getPhysicsLocation(
                        Vector3f()
                    ), collisionObject.getPhysicsRotation(
                        Quaternion()
                    )] = collisionObject.getPhysicsRotation(Quaternion())
                }
            } else if (collisionObject is TerrainRigidBody) {
                this.terrainMap[collisionObject.blockPos] = collisionObject
            }
            super.addCollisionObject(collisionObject)
        }
    }

    fun removePhysicsElement(element: PhysicsElement) {
        removeCollisionObject(element.rigidBody)
    }

    override fun removeCollisionObject(collisionObject: PhysicsCollisionObject) {
        if (collisionObject.isInWorld) {
            super.removeCollisionObject(collisionObject)
            val physicsElement = physicsElementMap[collisionObject]
            if (physicsElement == null) {
                if (collisionObject is TerrainRigidBody) {
                    this.removeTerrainObjectAt(collisionObject.blockPos)
                }
            }
        }
        physicsElementMap.remove(collisionObject)
    }

    fun doBlockUpdate(blockPos: BlockPos, blockState: BlockState) {
        blockCache.refreshBlockData(blockPos, blockState)
        fluidCache.refreshFluidData(blockPos)
        wakeNearbyElementRigidBodies(blockPos)
    }

    fun wakeNearbyElementRigidBodies(blockPos: BlockPos) {
        for (elementRigidBodyData in physicsElements) {
            if (!elementRigidBodyData.isTerrainLoadingEnabled) {
                continue
            }
            if (elementRigidBodyData.isNear(blockPos)) {
                elementRigidBodyData.rigidBody.activate()
            }
        }
    }

    fun getTerrainMap(): Map<BlockPos, TerrainRigidBody> {
        return HashMap(terrainMap)
    }

    fun getTerrainObjectAt(blockPos: BlockPos): TerrainRigidBody? {
        return terrainMap[blockPos]
    }

    fun removeTerrainObjectAt(blockPos: BlockPos) {
        val removed = terrainMap.remove(blockPos)
        if (removed != null) {
            this.removeCollisionObject(removed)
        }
    }

    val physicsElements: Collection<PhysicsElement>
        get() = physicsElementMap.values

    /**
     * Trigger all collision events (e.g. block/element or element/element).
     *
     * @param event the event context
     */
    override fun collision(event: PhysicsCollisionEvent) {
        val impulse = event.appliedImpulse
        //
//        /* Element on Element */
//        if (event.getObjectA() instanceof ElementRigidBody rigidBodyA && event.getObjectB() instanceof ElementRigidBody rigidBodyB) {
////            Bukkit.getPluginManager().callEvent(new ElementCollisionEvent(rigidBodyA.getElement(), rigidBodyB.getElement(), impulse));
//        }
//        /* Block on Element */
//        else if (event.getObjectA() instanceof TerrainRigidBody terrain && event.getObjectB() instanceof ElementRigidBody rigidBody) {
////            Bukkit.getPluginManager().callEvent(new BlockCollisionEvent(rigidBody.getElement(), terrain, impulse));
//        }
//        /* Element on Block */
//        else if (event.getObjectA() instanceof ElementRigidBody rigidBody && event.getObjectB() instanceof TerrainRigidBody terrain) {
////            Bukkit.getPluginManager().callEvent(new BlockCollisionEvent(rigidBody.getElement(), terrain, impulse));
//        }
    }
}