package dev.lazurite.rayon;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import dev.lazurite.rayon.impl.bullet.collision.body.PhysicsElement;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class RigidBodyDataStorage {
    private static final Map<PhysicsCollisionObject, PhysicsElement> RIGID_BODY_DATA_MAP = new HashMap<>();

    public PhysicsElement get(PhysicsCollisionObject physicsCollisionObject) {
        return RIGID_BODY_DATA_MAP.get(physicsCollisionObject);
    }

    public void add(PhysicsCollisionObject physicsCollisionObject, PhysicsElement physicsElement) {
        RIGID_BODY_DATA_MAP.put(physicsCollisionObject, physicsElement);
    }
}
