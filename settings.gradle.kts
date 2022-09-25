pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "RayonBukkit"
include("nms")
include("nms:v1_19_2")
findProject(":nms:v1_19_2")?.name = "v1_19_2"