package ru.melonhell.bulletphysics.bullet.collision.body.element

/**
 * A simple clock for keeping track of delta time in milliseconds and microseconds.
 *
 * @see MinecraftSpace
 */
class Clock {
    private var startTime: Long = 0

    init {
        reset()
    }

    /**
     * @return seconds since last called
     */
    fun get(): Float {
        return timeMicroseconds / 1000000f
    }

    val andReset: Float
        get() {
            val delta = get()
            reset()
            return delta
        }

    fun reset() {
        startTime = System.nanoTime()
    }

    val timeMilliseconds: Long
        get() = (System.nanoTime() - startTime) / 1000000L
    val timeMicroseconds: Long
        get() = (System.nanoTime() - startTime) / 1000L
}