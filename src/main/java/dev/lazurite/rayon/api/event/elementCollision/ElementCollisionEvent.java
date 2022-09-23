//package dev.lazurite.rayon.api.event.elementCollision;
//
//import dev.lazurite.rayon.api.PhysicsElement;
//import lombok.Getter;
//import org.bukkit.event.Event;
//import org.bukkit.event.HandlerList;
//
//@Getter
//public class ElementCollisionEvent extends Event {
//    private final PhysicsElement element1;
//    private final PhysicsElement element2;
//    private final float impulse;
//    private static final HandlerList HANDLERS = new HandlerList();
//
//    public ElementCollisionEvent(PhysicsElement element1, PhysicsElement element2, float impulse) {
////        super(true);
//        this.element1 = element1;
//        this.element2 = element2;
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
