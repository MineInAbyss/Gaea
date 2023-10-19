package org.cultofclang.minecraft.gaea

import com.mineinabyss.idofront.di.DI

val gaea by DI.observe<GaeaContext>()
interface GaeaContext {
    val plugin: GaeaPlugin
    val config: GaeaConfig
}
