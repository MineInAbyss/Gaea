package org.cultofclang.minecraft.gaea

import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import org.cultofclang.utils.MarketBook
import org.cultofclang.utils.calcMarket

val Gaea: GaeaPlugin by lazy { JavaPlugin.getPlugin(GaeaPlugin::class.java) }
val Broker: MarketBook by lazy { calcMarket() }
val ProtectionKey: NamespacedKey = NamespacedKey(Gaea, "regen_protected")
