package ru.melonhell.bulletphysics.init;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"ru.melonhell.bulletphysics"})
public class BulletPhysicsApplication {
    @Bean
    public PaperCommandManager paperCommandManager(JavaPlugin javaPlugin) {
        return new PaperCommandManager(javaPlugin);
    }
}
