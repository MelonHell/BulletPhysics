package dev.lazurite.rayon.impl.util;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.api.PhysicsElement;
import dev.lazurite.rayon.utils.math.MathShit;

/**
 * A {@link Frame} can be used for interpolation on the render thread.
 * {@link Frame}s are stored in {@link PhysicsElement}s and are updated
 * each tick.
 */
public class Frame {
    private Vector3f prevLocation;
    private Vector3f tickLocation;
    private Quaternion prevRotation;
    private Quaternion tickRotation;

    public Frame() {
        this(new Vector3f(), new Quaternion());
    }

    public Frame(Vector3f location, Quaternion rotation) {
        this.set(location, location, rotation, rotation);
    }

    public void set(Vector3f prevLocation, Vector3f tickLocation, Quaternion prevRotation, Quaternion tickRotation) {
        this.prevLocation = prevLocation;
        this.tickLocation = tickLocation;
        this.prevRotation = prevRotation;
        this.tickRotation = tickRotation;
    }

    public void from(Frame frame) {
        this.set(frame.prevLocation, frame.tickLocation, frame.prevRotation, frame.tickRotation);
    }

    public void from(Frame prevFrame, Vector3f tickLocation, Quaternion tickRotation) {
        this.set(prevFrame.tickLocation, tickLocation, prevFrame.tickRotation, tickRotation);
    }

    public Vector3f getLocation(Vector3f store, float tickDelta) {
        Vector3f lerp = MathShit.lerp(this.prevLocation, this.tickLocation, tickDelta);
        return store.set(lerp);
    }

    public Quaternion getRotation(Quaternion store, float tickDelta) {
        Quaternion slerp = MathShit.slerp(this.prevRotation, this.tickRotation, tickDelta);
        return store.set(slerp);
    }

    public Vector3f getLocationDelta(Vector3f store) {
        return store.set(tickLocation.subtract(prevLocation));
    }

    public Vector3f getRotationDelta(Vector3f store) {
        final var tickAngles = MathShit.toEulerAngles(this.tickRotation);
        final var prevAngles = MathShit.toEulerAngles(this.prevRotation);
        tickAngles.subtractLocal(prevAngles);
        return store.set(tickAngles);
    }
}