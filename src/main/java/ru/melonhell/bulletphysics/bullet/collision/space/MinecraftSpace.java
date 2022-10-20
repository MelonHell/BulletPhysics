package ru.melonhell.bulletphysics.bullet.collision.space;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import ru.melonhell.bulletphysics.bullet.collision.body.TerrainRigidBody;
import ru.melonhell.bulletphysics.bullet.collision.body.element.PhysicsElement;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.ChunkCache;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.SimpleChunkCache;
import ru.melonhell.bulletphysics.bullet.collision.space.generator.PressureGenerator;
import ru.melonhell.bulletphysics.bullet.collision.space.generator.TerrainGenerator;
import ru.melonhell.bulletphysics.bullet.thread.PhysicsThread;
import ru.melonhell.bulletphysics.nms.NmsTools;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPosWrapper;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is the main physics simulation used by Rayon. Each bullet simulation update
 * happens asynchronously while all of the setup, input, or otherwise user defined
 * behavior happens on the game logic thread.
 * <br><br>
 * It is also worth noting that another
 * simulation step will not be performed if the last step has taken longer than 50ms and is still executing upon the
 * next tick. This really only happens if you are dealing with an ungodly amount of rigid bodies or your computer is slo.
 *
 * @see PhysicsThread
 * see PhysicsSpaceEvents
 */
public class MinecraftSpace extends PhysicsSpace implements PhysicsCollisionListener {
    private final Map<BlockPosWrapper, TerrainRigidBody> terrainMap;
    private final Map<PhysicsCollisionObject, PhysicsElement> physicsElementMap = new HashMap<>();
    private final JavaPlugin javaPlugin;
    @Getter
    private final PhysicsThread physicsThread;
    @Getter
    private final TerrainGenerator terrainGenerator;
    @Getter
    private final PressureGenerator pressureGenerator;
    @Getter
    private final World world;
    @Getter
    private final ChunkCache chunkCache;

    private volatile boolean stepping;

    public MinecraftSpace(JavaPlugin javaPlugin, PhysicsThread physicsThread, NmsTools nmsTools, TerrainGenerator terrainGenerator, PressureGenerator pressureGenerator, World world) {
        super(BroadphaseType.DBVT);

        this.javaPlugin = javaPlugin;
        this.physicsThread = physicsThread;
        this.pressureGenerator = pressureGenerator;
        this.terrainGenerator = terrainGenerator;
        this.world = world;
        this.chunkCache = new SimpleChunkCache(this, nmsTools);
        this.terrainMap = new ConcurrentHashMap<>();
        this.setGravity(new Vector3f(0, -9.807f, 0));
        this.addCollisionListener(this);
        this.setAccuracy(1f / 60f);
    }

    /**
     * This method performs the following steps:
     * <ul>
     *     <li>Fires world step events in {link PhysicsSpaceEvents}.</li>
     *     <li>Steps {link ElementRigidBody}s.</li>
     *     <li>Steps the simulation asynchronously.</li>
     *     <li>Triggers collision events.</li>
     * </ul>
     * <p>
     * Additionally, none of the above steps execute when either the world is empty
     * (no {@link PhysicsRigidBody}s) or when the game is paused.
     *
     * @see TerrainGenerator
     * see PhysicsSpaceEvents
     */
    public void step() {
        Bukkit.getScheduler().runTaskAsynchronously(javaPlugin, () -> getElementRigidBodyDataList().forEach(PhysicsElement::updateFrame));

        if (!isStepping() && !isEmpty()) {
            this.stepping = true;

            // Hop threads...
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                /* Call collision events */
                this.distributeEvents();

                chunkCache.refreshAll();
                terrainGenerator.step(this);
                pressureGenerator.step(this);

                /* Step the Simulation */
                this.update(1 / 20f);
            }, getPhysicsThread());


            future.thenRun(() -> this.stepping = false);
        }
    }

    public void addPhysicsElement(PhysicsElement element) {
        addCollisionObject(element.getRigidBody());
        physicsElementMap.put(element.getRigidBody(), element);
    }

    @Override
    public void addCollisionObject(PhysicsCollisionObject collisionObject) {
        if (!collisionObject.isInWorld()) {
            PhysicsElement physicsElement = physicsElementMap.get(collisionObject);
            if (physicsElement != null) {

//                Bukkit.getPluginManager().callEvent(new PhysicsSpaceElementAddedEvent(this, rigidBody));

                if (!collisionObject.isInWorld()) {
                    collisionObject.activate(true);
                    physicsElement.getFrame().set(
                            collisionObject.getPhysicsLocation(new Vector3f()),
                            collisionObject.getPhysicsLocation(new Vector3f()),
                            collisionObject.getPhysicsRotation(new Quaternion()),
                            collisionObject.getPhysicsRotation(new Quaternion()));
                }
            } else if (collisionObject instanceof TerrainRigidBody terrain) {
                this.terrainMap.put(terrain.getBlockPos(), terrain);
            }

            super.addCollisionObject(collisionObject);
        }
    }

    public void removePhysicsElement(PhysicsElement element) {
        removeCollisionObject(element.getRigidBody());
        physicsElementMap.remove(element.getRigidBody());
    }

    @Override
    public void removeCollisionObject(PhysicsCollisionObject collisionObject) {
        if (collisionObject.isInWorld()) {
            super.removeCollisionObject(collisionObject);

            PhysicsElement physicsElement = physicsElementMap.get(collisionObject);
            if (physicsElement != null) {
//                Bukkit.getPluginManager().callEvent(new PhysicsSpaceElementRemovedEvent(this, rigidBody));
            } else if (collisionObject instanceof TerrainRigidBody terrain) {
                this.removeTerrainObjectAt(terrain.getBlockPos());
            }
        }
    }

    public boolean isStepping() {
        return this.stepping;
    }

    public void doBlockUpdate(BlockPosWrapper blockPos) {
        this.chunkCache.loadBlockData(blockPos);
        this.chunkCache.loadFluidData(blockPos);
        this.wakeNearbyElementRigidBodies(blockPos);
    }

    public void wakeNearbyElementRigidBodies(BlockPosWrapper blockPos) {
        for (var elementRigidBodyData : getElementRigidBodyDataList()) {
            if (!elementRigidBodyData.isTerrainLoadingEnabled()) {
                continue;
            }

            if (elementRigidBodyData.isNear(blockPos)) {
                elementRigidBodyData.getRigidBody().activate();
            }
        }
    }

    public Map<BlockPosWrapper, TerrainRigidBody> getTerrainMap() {
        return new HashMap<>(this.terrainMap);
    }

    public Optional<TerrainRigidBody> getTerrainObjectAt(BlockPosWrapper blockPos) {
        return Optional.ofNullable(terrainMap.get(blockPos));
    }

    public void removeTerrainObjectAt(BlockPosWrapper blockPos) {
        final var removed = terrainMap.remove(blockPos);

        if (removed != null) {
            this.removeCollisionObject(removed);
        }
    }

    public List<PhysicsElement> getElementRigidBodyDataList() {
        List<PhysicsElement> list = new ArrayList<>();
        for (var body : getRigidBodyList()) {
            PhysicsElement physicsElement = physicsElementMap.get(body);
            if (physicsElement != null) list.add(physicsElement);
        }
        return list;
    }

    /**
     * Trigger all collision events (e.g. block/element or element/element).
     *
     * @param event the event context
     */
    @Override
    public void collision(PhysicsCollisionEvent event) {
        float impulse = event.getAppliedImpulse();
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