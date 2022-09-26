package ru.melonhell.bulletphysics.storage;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import org.springframework.stereotype.Component;
import ru.melonhell.bulletphysics.bullet.collision.body.element.PhysicsElement;

import java.util.HashMap;
import java.util.Map;

@Component
public class RigidBodyDataStorage {
    private final Map<PhysicsCollisionObject, PhysicsElement> RIGID_BODY_DATA_MAP = new HashMap<>();

    public PhysicsElement get(PhysicsCollisionObject physicsCollisionObject) {
        return RIGID_BODY_DATA_MAP.get(physicsCollisionObject);
    }

    public void add(PhysicsCollisionObject physicsCollisionObject, PhysicsElement physicsElement) {
        RIGID_BODY_DATA_MAP.put(physicsCollisionObject, physicsElement);
    }
}
