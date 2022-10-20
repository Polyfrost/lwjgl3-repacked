plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
    id("signing")
    `java-library`
}

group = "cc.polyfrost"
version = "1.0.0-alpha22"

base {
    archivesName.set("lwjgl")
}

repositories {
    mavenCentral()
}

val shadeCompileOnly: Configuration by configurations.creating
val shadeSeparate: Configuration by configurations.creating

dependencies {
    val lwjglVersion = "3.3.1"

    shadeCompileOnly("org.lwjgl:lwjgl-stb:$lwjglVersion")
    shadeCompileOnly("org.lwjgl:lwjgl-tinyfd:$lwjglVersion")

    shadeCompileOnly("org.lwjgl:lwjgl-stb:$lwjglVersion:natives-windows")
    shadeCompileOnly("org.lwjgl:lwjgl-tinyfd:$lwjglVersion:natives-windows")
    shadeCompileOnly("org.lwjgl:lwjgl-stb:$lwjglVersion:natives-linux")
    shadeCompileOnly("org.lwjgl:lwjgl-tinyfd:$lwjglVersion:natives-linux")
    shadeCompileOnly("org.lwjgl:lwjgl-stb:$lwjglVersion:natives-macos")
    shadeCompileOnly("org.lwjgl:lwjgl-tinyfd:$lwjglVersion:natives-macos")
    shadeCompileOnly("org.lwjgl:lwjgl-stb:$lwjglVersion:natives-macos-arm64")
    shadeCompileOnly("org.lwjgl:lwjgl-tinyfd:$lwjglVersion:natives-macos-arm64")

    shadeCompileOnly("org.lwjgl:lwjgl:$lwjglVersion")
    shadeCompileOnly("org.lwjgl:lwjgl:$lwjglVersion:natives-windows")
    shadeCompileOnly("org.lwjgl:lwjgl:$lwjglVersion:natives-linux")
    shadeCompileOnly("org.lwjgl:lwjgl:$lwjglVersion:natives-macos")

    shadeCompileOnly("org.lwjgl:lwjgl-nanovg:$lwjglVersion") {
        isTransitive = false
    }
    shadeCompileOnly("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-windows") {
        isTransitive = false
    }
    shadeCompileOnly("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-linux") {
        isTransitive = false
    }
    shadeCompileOnly("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-macos") {
        isTransitive = false
    }

    // force 3.3.1 for this, because
    // if the user is actually running M1+, LWJGL must be 3.3.0+
    shadeSeparate("org.lwjgl:lwjgl:3.3.1:natives-macos-arm64")
    shadeSeparate("org.lwjgl:lwjgl-nanovg:3.3.1:natives-macos-arm64") {
        isTransitive = false
    }
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(shadeCompileOnly, shadeSeparate)
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
        register<MavenPublication>("lwjgl") {
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