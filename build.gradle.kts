plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.copyjar")
    id("com.mineinabyss.conventions.publication")
    id("com.mineinabyss.conventions.testing")
    kotlin("plugin.serialization")
}

repositories {
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://hub.jeff-media.com/nexus/repository/jeff-media-public/") //CustomBlockData
}

dependencies {
    // MineInAbyss platform
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    compileOnly(libs.exposed.core) { isTransitive = false }
    compileOnly(libs.exposed.dao) { isTransitive = false }
    compileOnly(libs.exposed.jdbc) { isTransitive = false }

    compileOnly(gaealibs.geary.papermc.core)
    compileOnly(gaealibs.blocky)
    compileOnly(gaealibs.minecraft.plugin.blocklocker)

    implementation(libs.idofront.core)
    implementation(gaealibs.minecraft.plugin.customblockdata)
    implementation(gaealibs.minecraft.plugin.morepersistentdatatypes)
}
