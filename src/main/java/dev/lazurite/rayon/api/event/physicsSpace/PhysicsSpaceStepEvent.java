//package dev.lazurite.rayon.api.event.physicsSpace;
//
//import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
//import lombok.Getter;
//import org.bukkit.event.Event;
//import org.bukkit.event.HandlerList;
//
//@Getter
//public class PhysicsSpaceStepEvent extends Event {
//    private final MinecraftSpace space;
//    private static final HandlerList HANDLERS = new HandlerList();
//
//    public PhysicsSpaceStepEvent(MinecraftSpace space) {
//        super(true);
//        this.space = space;
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
