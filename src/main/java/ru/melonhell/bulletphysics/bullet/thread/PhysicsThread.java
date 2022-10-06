package ru.melonhell.bulletphysics.bullet.thread;

import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

/**
 * In order to access an instance of this, all you need is a {link Level} or {link ReentrantBlockableEventLoop} object.
 * Calling {@link PhysicsThread#execute} adds a runnable to the queue of tasks and is the main way to execute code on
 * this thread. You can also execute code here by using {link PhysicsSpaceEvents}.
 * see PhysicsSpaceEvents
 * see PhysicsElement
 *
 * @see MinecraftSpace
 */
@Component
public class PhysicsThread extends Thread implements Executor {
    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    public volatile Throwable throwable;
    public volatile boolean running = true;

    @PostConstruct
    public void init() {
        this.setName("Server Physics Thread");
        this.setUncaughtExceptionHandler((thread, throwable) -> {
            this.running = false;
            this.throwable = throwable;
        });

        LogManager.getLogger("BulletPhysics").info("Starting " + getName());
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
     *
     * @param task the task to run
     */
    @Override
    public void execute(@NotNull Runnable task) {
        tasks.add(task);
    }


    /**
     * Join the thread when the game closes.
     */
    @PreDestroy
    public void destroy() {
        this.running = false;
        LogManager.getLogger("BulletPhysics").info("Stopping " + getName());

        try {
            this.join(5000); // 5 second timeout
        } catch (InterruptedException e) {
            LogManager.getLogger("BulletPhysics").error("Error joining " + getName());
            e.printStackTrace();
        }
    }
}
