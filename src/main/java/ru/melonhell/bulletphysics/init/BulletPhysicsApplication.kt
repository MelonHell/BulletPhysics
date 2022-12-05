package ru.melonhell.bulletphysics.init

import co.aikar.commands.PaperCommandManager
import org.bukkit.plugin.java.JavaPlugin
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication(scanBasePackages = ["ru.melonhell.bulletphysics"])
open class BulletPhysicsApplication {
    @Bean
    open fun paperCommandManager(javaPlugin: JavaPlugin?): PaperCommandManager {
        return PaperCommandManager(javaPlugin)
    }
}