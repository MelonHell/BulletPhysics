//package dev.lazurite.rayon.api.event.elementCollision;
//
//import dev.lazurite.rayon.api.PhysicsElement;
//import dev.lazurite.rayon.impl.bullet.collision.body.TerrainRigidBody;
//import lombok.Getter;
//import org.bukkit.event.Event;
//import org.bukkit.event.HandlerList;
//
//@Getter
//public class BlockCollisionEvent extends Event {
//    private final PhysicsElement element;
//    private final TerrainRigidBody terrainObject;
//    private final float impulse;
//    private static final HandlerList HANDLERS = new HandlerList();
//
//    public BlockCollisionEvent(PhysicsElement element, TerrainRigidBody terrainObject, float impulse) {
////        super(true);
//        this.element = element;
//        this.terrainObject = terrainObject;
//        this.impulse = impulse;
//    }
//
//    public static HandlerList getHandlerList() {
//        return HANDLERS;
//    }
//
//    @Override
//    public HandlerList getHandlers() {
//        return HANDLERS;
//    }
//}
