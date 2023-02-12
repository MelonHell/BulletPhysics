import groovy.util.Node
import groovy.util.NodeList

plugins {
    kotlin("jvm") version "1.7.20"
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

group = "ru.melonhell"

allprojects {
    apply(plugin = "kotlin")

    version = "1.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://lazurite.dev/releases")
        maven("https://repo.aikar.co/content/groups/aikar/")
        maven("https://repo.spliterash.ru/group/")
    }
    dependencies {
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.20")
        compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
        compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")
        compileOnly("ru.spliterash:spring-spigot:1.0.6")
        implementation("com.github.stephengold:Libbulletjme:17.4.0")
    }
}

bukkit {
    name = "BulletPhysics"
    main = "ru.melonhell.bulletphysics.init.BulletPhysicsPlugin"
    apiVersion = "1.13"
    authors = listOf("MelonHell")
    depend = listOf("SpringSpigot")
    libraries = listOf(
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.20",
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4",
        "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4",
    )
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
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
        relocate("co.aikar.commands", "ru.melonhell.bulletphysics.libs.acf")
        relocate("co.aikar.locales", "ru.melonhell.bulletphysics.libs.acf.locales")
    }
    assemble {
        dependsOn(shadowJar)
    }
    jar {
        enabled = false
    }
    withType<GenerateModuleMetadata> {
        enabled = false
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = rootProject.name

            from(components["java"])
            setArtifacts(listOf(tasks.shadowJar))

            pom {
                withXml {
                    val root = asNode()
                    val dependenciesNodeList = root.get("dependencies") as NodeList
                    if (!dependenciesNodeList.isEmpty()) {
                        val dependenciesNode = dependenciesNodeList[0] as Node

                        root.remove(dependenciesNode)
                    }
                }
            }
        }
    }

    repositories {
        maven {
            name = "nexus"
            url = uri("https://repo.spliterash.ru/" + rootProject.name)
            credentials {
                username = findProperty("SPLITERASH_NEXUS_USR")?.toString()
                password = findProperty("SPLITERASH_NEXUS_PSW")?.toString()
            }
        }
    }
}