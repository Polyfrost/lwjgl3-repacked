pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven("https://repo.polyfrost.cc/releases")
    }
    plugins {
        val egtVersion = "0.1.28"
        id("cc.polyfrost.multi-version.root") version egtVersion
        id("cc.polyfrost.defaults.repo") version egtVersion
        id("cc.polyfrost.defaults.java") version egtVersion
        id("cc.polyfrost.multi-version.api-validation") version egtVersion
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "io.github.juuxel.loom-quiltflower-mini") {
                useModule("com.github.wyvest:loom-quiltflower-mini:${requested.version}")
            }
        }
    }
}

rootProject.name = "lwjgl3-repacked"
rootProject.buildFileName = "root.gradle.kts"

listOf(
    "legacy",
    "pre-1.19-noarm",
    "pre-1.19-arm",
    "post-1.19"
).forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle.kts"
    }
}
