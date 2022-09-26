package dev.lazurite.rayon.impl.bullet.collision.body;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.impl.bullet.thread.util.Clock;
import dev.lazurite.rayon.impl.util.Frame;
import dev.lazurite.rayon.nms.wrappers.AABBWrapper;
import dev.lazurite.rayon.nms.wrappers.BlockPosWrapper;
import dev.lazurite.rayon.utils.math.Convert;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhysicsElement {
    public static final float SLEEP_TIME_IN_SECONDS = 2.0f;

    private final PhysicsRigidBody rigidBody;
    protected final MinecraftSpace space;
    private final Frame frame = new Frame();
    private final Clock sleepTimer = new Clock();
    private boolean terrainLoadingEnabled;
    private float dragCoefficient = 0.25f;
    private BuoyancyType buoyancyType = BuoyancyType.WATER;
    private DragType dragType = DragType.SIMPLE;

    public PhysicsElement(PhysicsRigidBody rigidBody, MinecraftSpace space) {
        this.rigidBody = rigidBody;
        this.space = space;
        this.terrainLoadingEnabled = !rigidBody.isStatic();
    }

    public void updateFrame() {
        getFrame().from(getFrame(), rigidBody.getPhysicsLocation(new Vector3f()), rigidBody.getPhysicsRotation(new Quaternion()));
    }

    public boolean isNear(BlockPosWrapper blockPos) {
        return Convert.toMinecraft(rigidBody.boundingBox(new BoundingBox())).intersects(new AABBWrapper(blockPos).inflate(0.5f));
    }

    public boolean isWaterBuoyancyEnabled() {
        return buoyancyType == BuoyancyType.WATER || buoyancyType == BuoyancyType.ALL;
    }

    public boolean isAirBuoyancyEnabled() {
        return buoyancyType == BuoyancyType.AIR || buoyancyType == BuoyancyType.ALL;
    }

    public boolean isWaterDragEnabled() {
        // We check for simple drag here, but complex drag is always used for water buoyancy.
        return dragType == DragType.WATER || dragType == DragType.ALL || dragType == DragType.SIMPLE;
    }

    public boolean isAirDragEnabled() {
        return dragType == DragType.AIR || dragType == DragType.ALL;
    }


    public MinecraftShape getMinecraftShape() {
        return (MinecraftShape) rigidBody.getCollisionShape();
    }

    public enum BuoyancyType {
        NONE,
        AIR,
        WATER,
        ALL
    }

    public enum DragType {
        NONE,
        AIR,
        WATER,
        SIMPLE,
        ALL
    }
}