package ru.melonhell.bulletphysics.bullet.collision.body.element

import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import ru.melonhell.bulletphysics.utils.math.MathShit.lerp
import ru.melonhell.bulletphysics.utils.math.MathShit.slerp
import ru.melonhell.bulletphysics.utils.math.MathShit.toEulerAngles

/**
 * A [Frame] can be used for interpolation on the render thread.
 * [Frame]s are stored in {link PhysicsElement}s and are updated
 * each tick.
 */
class Frame(
    location: Vector3f = Vector3f(),
    rotation: Quaternion = Quaternion()
) {
    private var prevLocation: Vector3f = Vector3f()
    private var tickLocation: Vector3f = Vector3f()
    private var prevRotation: Quaternion = Quaternion()
    private var tickRotation: Quaternion = Quaternion()

    init {
        this[location, location, rotation] = rotation
    }

    operator fun set(
        prevLocation: Vector3f,
        tickLocation: Vector3f,
        prevRotation: Quaternion,
        tickRotation: Quaternion
    ) {
        this.prevLocation = prevLocation
        this.tickLocation = tickLocation
        this.prevRotation = prevRotation
        this.tickRotation = tickRotation
    }

    fun from(frame: Frame) {
        this[frame.prevLocation, frame.tickLocation, frame.prevRotation] = frame.tickRotation
    }

    fun from(prevFrame: Frame, tickLocation: Vector3f, tickRotation: Quaternion) {
        this[prevFrame.tickLocation, tickLocation, prevFrame.tickRotation] = tickRotation
    }

    fun getLocation(store: Vector3f, tickDelta: Float): Vector3f {
        val lerp = lerp(prevLocation, tickLocation, tickDelta)
        return store.set(lerp)
    }

    fun getRotation(store: Quaternion, tickDelta: Float): Quaternion {
        val slerp = slerp(prevRotation, tickRotation, tickDelta)
        return store.set(slerp)
    }

    fun getLocationDelta(store: Vector3f): Vector3f {
        return store.set(tickLocation.subtract(prevLocation))
    }

    fun getRotationDelta(store: Vector3f): Vector3f {
        val tickAngles = toEulerAngles(tickRotation)
        val prevAngles = toEulerAngles(prevRotation)
        tickAngles.subtractLocal(prevAngles)
        return store.set(tickAngles)
    }
}