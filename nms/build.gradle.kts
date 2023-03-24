plugins {
    id("io.papermc.paperweight.userdev") version "1.3.8" apply false
}

val currentProject = project;

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
}

subprojects {
    apply(plugin = "io.papermc.paperweight.userdev")

    dependencies {
        api(currentProject)
    }
}
