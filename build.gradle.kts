plugins {
    id("java-library")
    id("io.freefair.lombok") version "6.3.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
//    id("io.papermc.paperweight.userdev") version "1.3.8"
}

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "io.freefair.lombok")
    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://lazurite.dev/releases")
        maven("https://repo.aikar.co/content/groups/aikar/")
        maven {
            url = uri("https://nexus.spliterash.ru/repository/all/")
            credentials {
                username = findProperty("SPLITERASH_NEXUS_USR")?.toString()
                password = findProperty("SPLITERASH_NEXUS_PSW")?.toString()
            }
        }
    }
}

bukkit {
    name = "BulletPhysics"
    main = "ru.melonhell.bulletphysics.init.BulletPhysicsPlugin"
    apiVersion = "1.13"
    authors = listOf("MelonHell")
    depend = listOf("SpringSpigot")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
    compileOnly("ru.spliterash:spring-spigot:1.0.2")
    implementation("com.github.stephengold:Libbulletjme:16.2.1")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    api(project(":nms"))
    api(project(":nms:v1_19_2", "reobf"))
}

tasks {
    shadowJar {
        relocate("co.aikar.commands", "dev.lazurite.rayon.libs.acf")
        relocate("co.aikar.locales", "dev.lazurite.rayon.libs.acf.locales")
    }
}

tasks.assemble.get().dependsOn(tasks.shadowJar)