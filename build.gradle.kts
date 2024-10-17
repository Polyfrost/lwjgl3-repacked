plugins {
    id("cc.polyfrost.multi-version")
    id("cc.polyfrost.defaults.repo")
    id("cc.polyfrost.defaults.java")
    id("cc.polyfrost.defaults.loom")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
    id("signing")
    `java-library`
}

group = "cc.polyfrost"
version = "1.0.0-alpha26"

val noArm = project.name.contains("noarm")

base {
    archivesName.set("lwjgl-${project.name}")
}

repositories {
    mavenCentral()
}

val shade: Configuration by configurations.creating

dependencies {
    val lwjglVersion = when (platform.mcVersion) {
        in 10809..11202 -> "3.3.1"
        in 11203..11802 -> if (noArm) "3.2.1" else "3.3.1"
        else -> "3.3.1"
    }

    if (platform.mcVersion <= 11202) {
        shade("org.lwjgl:lwjgl-stb:$lwjglVersion")
        shade("org.lwjgl:lwjgl-tinyfd:$lwjglVersion")

        shade("org.lwjgl:lwjgl-stb:$lwjglVersion:natives-windows")
        shade("org.lwjgl:lwjgl-tinyfd:$lwjglVersion:natives-windows")
        shade("org.lwjgl:lwjgl-stb:$lwjglVersion:natives-windows-x86")
        shade("org.lwjgl:lwjgl-tinyfd:$lwjglVersion:natives-windows-x86")
        shade("org.lwjgl:lwjgl-stb:$lwjglVersion:natives-windows-arm64")
        shade("org.lwjgl:lwjgl-tinyfd:$lwjglVersion:natives-windows-arm64")
        shade("org.lwjgl:lwjgl-stb:$lwjglVersion:natives-linux")
        shade("org.lwjgl:lwjgl-tinyfd:$lwjglVersion:natives-linux")
        shade("org.lwjgl:lwjgl-stb:$lwjglVersion:natives-macos")
        shade("org.lwjgl:lwjgl-tinyfd:$lwjglVersion:natives-macos")
        shade("org.lwjgl:lwjgl-stb:$lwjglVersion:natives-macos-arm64")
        shade("org.lwjgl:lwjgl-tinyfd:$lwjglVersion:natives-macos-arm64")

        shade("org.lwjgl:lwjgl:$lwjglVersion")
        shade("org.lwjgl:lwjgl:$lwjglVersion:natives-windows")
        shade("org.lwjgl:lwjgl:$lwjglVersion:natives-windows-x86")
        shade("org.lwjgl:lwjgl:$lwjglVersion:natives-windows-arm64")
        shade("org.lwjgl:lwjgl:$lwjglVersion:natives-linux")
        shade("org.lwjgl:lwjgl:$lwjglVersion:natives-macos")
        shade("org.lwjgl:lwjgl:$lwjglVersion:natives-macos-arm64")
    }

    shade("org.lwjgl:lwjgl-nanovg:$lwjglVersion") {
        isTransitive = false
    }
    if (platform.mcVersion !in 11203..11802 || noArm) {
        shade("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-windows") {
            isTransitive = false
        }
        shade("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-linux") {
            isTransitive = false
        }
        shade("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-macos") {
            isTransitive = false
        }
    }

    if (lwjglVersion != "3.2.1") {
        shade("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-macos-arm64") {
            isTransitive = false
        }
        shade("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-windows-x86") {
            isTransitive = false
        }
        shade("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-windows-arm64") {
            isTransitive = false
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