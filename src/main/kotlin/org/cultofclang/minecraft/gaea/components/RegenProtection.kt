package org.cultofclang.minecraft.gaea.components

import com.mineinabyss.idofront.serialization.DurationSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
@SerialName("gaea:regen_protection")
class RegenProtection(
    val protectionRadius: Int,
    //TODO Copy this as its only thing needed from Idofront?
    @Serializable(with = DurationSerializer::class)
    val protectionStarted: Duration
)
