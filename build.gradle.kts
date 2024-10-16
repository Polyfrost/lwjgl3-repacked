plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
    id("signing")
    `java-library`
}

group = "cc.polyfrost"
version = "1.0.0-alpha28"

base {
    archivesName.set("lwjgl-${project.name}")
}

repositories {
    mavenCentral()
}

val shade: Configuration by configurations.creating

dependencies {
    val version = "3.3.3"
    val platforms = listOf("windows", "windows-arm64", "windows-x86", "linux", "linux-arm64", "macos", "macos-arm64")
    val dependencies = mutableListOf("-nanovg")
    if (project.name == "legacy") {
        dependencies += listOf("-stb", "-tinyfd", "")
    }

    for (dep in dependencies) {
        val dependency = "org.lwjgl:lwjgl$dep:$version"
        shade(dependency)
        for (platform in platforms) {
            shade("$dependency:natives-$platform")
        }
    }
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(shade)
        exclude("META-INF/versions/**")
        exclude("**/module-info.class")
        exclude("**/package-info.class")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        dependsOn(jar)
    }
    jar {
        enabled = false
    }

    getByName("build").dependsOn(shadowJar)
}

publishing {
    publications {
        register<MavenPublication>("lwjgl-${project.name}") {
            groupId = project.group.toString()
            artifactId = base.archivesName.get()
            artifact(tasks["shadowJar"])
        }
    }

    repositories {
        maven {
            name = "releases"
            url = uri("https://repo.polyfrost.cc/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
        maven {
            name = "snapshots"
            url = uri("https://repo.polyfrost.cc/snapshots")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
        maven {
            name = "private"
            url = uri("https://repo.polyfrost.cc/private")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}