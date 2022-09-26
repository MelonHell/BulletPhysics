package ru.melonhell.bulletphysics.init;

import ru.spliterash.springspigot.init.SpringSpigotPlugin;

public class BulletPhysicsPlugin extends SpringSpigotPlugin {
    @Override
    protected Class<?> getAppClass() {
        return BulletPhysicsApplication.class;
    }
}
