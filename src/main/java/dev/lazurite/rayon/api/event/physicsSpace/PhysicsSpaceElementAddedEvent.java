//package dev.lazurite.rayon.api.event.physicsSpace;
//
//import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
//import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
//import lombok.Getter;
//import org.bukkit.event.Event;
//import org.bukkit.event.HandlerList;
//
//@Getter
//public class PhysicsSpaceElementAddedEvent extends Event {
//    private final MinecraftSpace space;
//    private final ElementRigidBody rigidBody;
//    private static final HandlerList HANDLERS = new HandlerList();
//
//    public PhysicsSpaceElementAddedEvent(MinecraftSpace space, ElementRigidBody rigidBody) {
////        super(true);
//        this.space = space;
//        this.rigidBody = rigidBody;
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
