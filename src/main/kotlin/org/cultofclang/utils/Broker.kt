package org.cultofclang.utils

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.*
import java.lang.Double.min


typealias Mats = Map<Material, Int>


operator fun Mats.plus(itemStack: ItemStack): Mats {
    val out =  this.toMutableMap()
    out.plus(itemStack.type, itemStack.amount)
    return out
}

operator fun Mats.plus(other: Mats): Mats {
    val keys = this.keys union other.keys
    return keys.map {
        it to (this[it]?:0) + (other[it]?:0)
    }.toMap()
}

operator fun Mats.unaryMinus(): Mats {
    return this.map { it.key to -it.value }.toMap()
}

operator fun Mats.minus(other: Mats): Mats {
    return this + (-other)
}


@Serializable
data class SimpleRecipe(
        val output: Mat,
        val inputs: Mats){
    override fun toString(): String {
        return stringify()
    }

    fun stringify() = Yaml.default.stringify(serializer(), this)

   // val gains by lazy { outputs - inputs }


    val inmats get() = inputs.keys
}

typealias Mat = Pair<Material, Int>

@Serializable
data class MarketBook(val values:Map<Material, Double>, val recipes:List<SimpleRecipe>){
    override fun toString(): String {
        return Yaml.default.stringify(MarketBook.serializer(), this)
    }

    fun value(stack: ItemStack)= stack.amount * (this.values[stack.type]?:0.0)
}

val ItemStack.m: Mat get() = Pair(type, amount )

fun calcMarket(): MarketBook {
    val value:MutableMap<Material, Double> = mutableMapOf(
            Material.IRON_ORE to 12.0,
            Material.GOLD_ORE to 30.0,

            //drop more then 1 when you break. kind surprised this is all of them
            Material.COAL_ORE to 30.0,
            Material.DIAMOND_ORE to 640.0,
            Material.LAPIS_ORE to 120.0,
            Material.EMERALD_ORE to 800.0,
            Material.NETHER_QUARTZ_ORE to 40.0,
            Material.REDSTONE_ORE to 60.0,
            Material.OAK_LOG to 8.0,
            Material.COBBLESTONE to 1.0,
            Material.DIRT to 1.0,
            Material.GRASS to 1.0,
            Material.SAND to 1.0,
            Material.GRAVEL to 1.0,
            Material.ICE to 1.0,
            Material.CLAY_BALL to 3.0,
            Material.VINE to 5.0
    )
    val recipes = Bukkit.getServer().recipeIterator().asSequence().mapNotNull {it.simple} .toList()


    for(i in 1..10){
        for (recipe in recipes) {
            if (value.keys.containsAll(recipe.inmats)) {
                val got = recipe.output
                val (outputMat, outCount) = got

                val cost = recipe.inputs
                        .map { it.value * value[it.key]!! }
                        .sum()

                val costPer = cost / outCount

                value[outputMat] = min(value[outputMat] ?: costPer, costPer)

                // well it's obvious that we did 911

                // need to add mats +
            }
        }
    }

    return MarketBook(
            recipes = recipes,
            values = value)
}


fun <T> MutableMap<T, Int>.plus(key:T, x: Int) {
   set(key, getOrDefault(key, 0) + x)
}


val Recipe.simple: SimpleRecipe?
    get() {
        return when (this) {
            is ShapelessRecipe -> simple
            is ShapedRecipe -> simple
            is CookingRecipe<*> -> simple
            is StonecuttingRecipe -> simple
            else -> null
        }
    }


val EmptyMats: Mats get() = mapOf()

fun List<ItemStack>.sum(): Mats {
    return this.groupBy { it.type }.map { it.key to it.value.sumBy {a -> a.amount } }.toMap()
}

val StonecuttingRecipe.simple: SimpleRecipe
    get() = SimpleRecipe(output = result.m, inputs = EmptyMats + input)

val CookingRecipe<*>.simple: SimpleRecipe
    get() = SimpleRecipe(output = result.m, inputs = EmptyMats + input)

val ShapedRecipe.ingredientList: List<ItemStack>
    get() = shape.joinToString().mapNotNull { ingredientMap[it] }

val ShapedRecipe.simple: SimpleRecipe
    get() = SimpleRecipe(
            output = result.m,
            inputs = ingredientList.sum()
    )

val ShapelessRecipe.simple: SimpleRecipe
    get() = SimpleRecipe(
            output = result.m,
            inputs = ingredientList.sum()
    )