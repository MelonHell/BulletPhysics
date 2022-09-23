package dev.lazurite.rayon.api.event.elementCollision;

import dev.lazurite.rayon.api.PhysicsElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class ElementCollisionEvent extends Event {
    private final PhysicsElement element1;
    private final PhysicsElement element2;
    private final float impulse;
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
