
plugins {
    alias(libs.plugins.mia.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.mia.copyjar)
    alias(libs.plugins.mia.publication)
    alias(libs.plugins.mia.autoversion)
    alias(libs.plugins.mia.testing)
    alias(libs.plugins.mia.papermc)
}

dependencies {
    // MineInAbyss platform
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    compileOnly(libs.exposed.core) { isTransitive = false }
    compileOnly(libs.exposed.dao) { isTransitive = false }
    compileOnly(libs.exposed.jdbc) { isTransitive = false }

    implementation(libs.bundles.idofront.core)
}
