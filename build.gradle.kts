plugins {
    id("java-library")
    id("io.freefair.lombok") version "6.3.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "io.freefair.lombok")

    version = "SNAPSHOT"

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
    dependencies {
        compileOnly("ru.spliterash:spring-spigot:1.0.2")
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

    implementation("com.github.stephengold:Libbulletjme:16.2.1")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    api(project(":nms"))
}

project(":nms").subprojects.forEach {
    rootProject.dependencies {
        api(project(it.path, "reobf"))
    }
}

tasks {
    shadowJar {
        archiveVersion.set("")
        archiveClassifier.set("")
        relocate("co.aikar.commands", "dev.lazurite.rayon.libs.acf")
        relocate("co.aikar.locales", "dev.lazurite.rayon.libs.acf.locales")
    }
    assemble {
        dependsOn(shadowJar)
    }
    jar {
        enabled = false
    }
}