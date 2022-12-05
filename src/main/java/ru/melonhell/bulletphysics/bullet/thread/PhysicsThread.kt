package ru.melonhell.bulletphysics.bullet.thread

import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Component
import java.lang.Thread.UncaughtExceptionHandler
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * In order to access an instance of this, all you need is a {link Level} or {link ReentrantBlockableEventLoop} object.
 * Calling [PhysicsThread.execute] adds a runnable to the queue of tasks and is the main way to execute code on
 * this thread. You can also execute code here by using {link PhysicsSpaceEvents}.
 * see PhysicsSpaceEvents
 * see PhysicsElement
 *
 * @see MinecraftSpace
 */
@Component
class PhysicsThread : Thread(), Executor {
    private val tasks: Queue<Runnable> = ConcurrentLinkedQueue()

    @Volatile
    var throwable: Throwable? = null

    @Volatile
    var running = true
    @PostConstruct
    fun init() {
        name = "Server Physics Thread"
        uncaughtExceptionHandler = UncaughtExceptionHandler { _, throwable ->
            running = false
            this.throwable = throwable
        }
        LogManager.getLogger("BulletPhysics").info("Starting $name")
        start()
    }

    /**
     * The worker loop. Waits for tasks and executes right away.
     */
    override fun run() {
        while (running) {
            /* Run all queued tasks */
            while (!tasks.isEmpty()) {
                tasks.poll().run()
            }
        }
    }

    /**
     * For queueing up tasks to be executed on this thread. A [MinecraftSpace]
     * object is provided within the consumer.
     *
     * @param task the task to run
     */
    override fun execute(task: Runnable) {
        tasks.add(task)
    }

    /**
     * Join the thread when the game closes.
     */
    @PreDestroy
    fun destroy() {
        running = false
        LogManager.getLogger("BulletPhysics").info("Stopping $name")
        try {
            this.join(5000) // 5 second timeout
        } catch (e: InterruptedException) {
            LogManager.getLogger("BulletPhysics").error("Error joining $name")
            e.printStackTrace()
        }
    }
}