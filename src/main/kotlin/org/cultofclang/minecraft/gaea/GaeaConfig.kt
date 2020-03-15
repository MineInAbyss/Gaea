package org.cultofclang.minecraft.gaea

import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class GaeaConfig(
        val baseDecayProbability: Float = 0.5f,
        val blocksDecayProbability:Map<Material, Float>,
        val masterWorld:Map<String, String>,
        val decayTimeSecs:Float,
        val decayRate:Float,
        val maxDecayPerTick:Int
){
    fun getDecayProbability(mat:Material) = blocksDecayProbability.getOrDefault(mat, baseDecayProbability)
}
/*
Breaking - 1 hour
Placing - 1 day
Entering - 1s /s
 */