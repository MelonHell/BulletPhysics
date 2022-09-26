package dev.lazurite.rayon.impl.bullet.thread;

import dev.lazurite.rayon.RayonPlugin;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

/**
 * In order to access an instance of this, all you need is a {link Level} or {link ReentrantBlockableEventLoop} object.
 * Calling {@link PhysicsThread#execute} adds a runnable to the queue of tasks and is the main way to execute code on
 * this thread. You can also execute code here by using {link PhysicsSpaceEvents}.
 * see PhysicsSpaceEvents
 * see PhysicsElement
 * @see MinecraftSpace
 */
public class PhysicsThread extends Thread implements Executor {
    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    private final Thread parentThread;

    public volatile Throwable throwable;
    public volatile boolean running = true;

    public static PhysicsThread get() {
        return RayonPlugin.getThread();
    }

    public static PhysicsThread get(World world) {
        return MinecraftSpace.get(world).getPhysicsThread();
    }

    public PhysicsThread(Thread parentThread, String name) {
        this.parentThread = parentThread;

        this.setName(name);
        this.setUncaughtExceptionHandler((thread, throwable) -> {
            this.running = false;
            this.throwable = throwable;
        });

        RayonPlugin.LOGGER.info("Starting " + getName());
        this.start();
    }

    /**
     * The worker loop. Waits for tasks and executes right away.
     */
    @Override
    public void run() {
        while (running) {
            /* Run all queued tasks */
            while (!tasks.isEmpty()) {
                tasks.poll().run();
            }
        }
    }

    /**
     * For queueing up tasks to be executed on this thread. A {@link MinecraftSpace}
     * object is provided within the consumer.
     * @param task the task to run
     */
    @Override
    public void execute(@NotNull Runnable task) {
        tasks.add(task);
    }

    /**
     * Gets the parent thread. This is useful for checking
     * whether a method is executing on this thread.
     * see EntitySupplier
     * @return the parent {@link Thread} object
     */
    public Thread getParentThread() {
        return this.parentThread;
    }

    /**
     * Join the thread when the game closes.
     */
    public void destroy() {
        this.running = false;
        RayonPlugin.LOGGER.info("Stopping " + getName());

        try {
            this.join(5000); // 5 second timeout
        } catch (InterruptedException e) {
            RayonPlugin.LOGGER.error("Error joining " + getName());
            e.printStackTrace();
        }
    }
}
