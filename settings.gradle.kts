pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven("https://repo.polyfrost.cc/releases")
    }
}

rootProject.name = "lwjgl3-repacked"
listOf("legacy", "modern").forEach { platform ->
    include(":$platform")
    project(":$platform").apply {
        projectDir = file("platforms/$platform")
        buildFileName = "../../build.gradle.kts"
    }
}
