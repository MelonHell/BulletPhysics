package dev.lazurite.rayon.toolbox.api.event;

import dev.lazurite.rayon.toolbox.impl.event.EventImpl;

/**
 * A simple, array-backed event for use in Lazurite mods.
 * @param <T> the functional interface
 * @see EventImpl
 * @since 1.2.3
 */
public interface Event<T> {
    static <T> Event<T> create() {
        return new EventImpl<>();
    }

    void register(T t);
    void invoke(Object... params);
}