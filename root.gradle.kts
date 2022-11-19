plugins {
    kotlin("jvm") version "1.6.21" apply false
    id("cc.polyfrost.multi-version.root")
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
    id("io.github.juuxel.loom-quiltflower-mini") version "171a6e2e49" apply false
}

preprocess {
    val legacy = createNode("legacy", 10809, "yarn")
    val pre119 = createNode("pre-1.19-noarm", 11602, "yarn")
    val pre119Arm = createNode("pre-1.19-arm", 11602, "yarn")
    val post119 = createNode("post-1.19", 11902, "yarn")

    post119.link(pre119)
    pre119Arm.link(pre119)
    pre119.link(legacy)
}