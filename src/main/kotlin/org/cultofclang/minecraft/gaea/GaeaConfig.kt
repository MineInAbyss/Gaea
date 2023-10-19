package org.cultofclang.minecraft.gaea

import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class GaeaConfig(
    val baseDecayProbability: Float = 0.5f,
    val blocksDecayProbability: Map<Material, Float> = mapOf(
        Material.DIRT to 1f,
        Material.COBBLESTONE to 1f,
        Material.GRASS to 1f,
        Material.WATER to 1f,
        Material.CHEST to 0f
    ),
    val masterWorld: Map<String, String> = mapOf("world" to "master_world"),
    val decayTimeBlockPlace: Float = 86400f,
    val decayTimeBlockBreak: Float = 3600f,
    val maxDecayTime: Float = 2592000f,
    val maxDecayPerTick: Int = 10,
    val timeBetweenDecay: Float = 86400f,
    val claimTime: Float = 8035200f,
    val claimRadius: Int = 16,
    val claimCost: Double = 64.0,
) {
    fun getDecayProbability(mat: Material) = blocksDecayProbability.getOrDefault(mat, baseDecayProbability)
}
/*
Breaking - 1 hour
Placing - 1 day
Entering - 1s /s
 */
