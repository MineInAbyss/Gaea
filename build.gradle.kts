import Com_mineinabyss_conventions_platform_gradle.Deps

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.copyjar")
    id("com.mineinabyss.conventions.publication")
    id("com.mineinabyss.conventions.testing")
    kotlin("plugin.serialization")
}
dependencies {
    // MineInAbyss platform
    compileOnly(Deps.kotlin.stdlib)
    compileOnly(Deps.kotlinx.serialization.json)
    compileOnly(Deps.kotlinx.serialization.kaml)
    compileOnly(Deps.exposed.core) { isTransitive = false }
    compileOnly(Deps.exposed.dao) { isTransitive = false }
    compileOnly(Deps.exposed.jdbc) { isTransitive = false }
}
