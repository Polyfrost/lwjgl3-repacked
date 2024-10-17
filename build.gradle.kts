plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
    id("signing")
    `java-library`
}

group = "cc.polyfrost"
version = "1.0.0-alpha29"

base {
    archivesName.set("lwjgl-legacy")
}

repositories {
    mavenCentral()
}

val shade: Configuration by configurations.creating

dependencies {
    val lwjglVersion = "3.3.1"

    for (module in listOf("", "-stb", "-tinyfd", "-nanovg")) {
        shade("org.lwjgl:lwjgl$module:$lwjglVersion")
        for (plaform in listOf("windows", "windows-x86", "windows-arm64", "linux", "linux-arm64", "linux-arm32", "macos", "macos-arm64")) {
            shade("org.lwjgl:lwjgl$module:$lwjglVersion:natives-$plaform")
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
            url = uri("https://repo.polyfrost.org/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
        maven {
            name = "snapshots"
            url = uri("https://repo.polyfrost.org/snapshots")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
        maven {
            name = "private"
            url = uri("https://repo.polyfrost.org/private")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}