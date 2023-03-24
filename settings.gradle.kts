pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "BulletPhysics"
include("nms")
include("nms:v1_19_4")
