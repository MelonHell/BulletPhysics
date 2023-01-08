package ru.melonhell.bulletphysics.init

import ru.spliterash.springspigot.init.SpringSpigotPlugin

class BulletPhysicsPlugin : SpringSpigotPlugin() {
    override fun getAppClass() = BulletPhysicsApplication::class.java
}