package org.cultofclang.minecraft.gaea

import com.jeff_media.customblockdata.CustomBlockData
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin
import org.cultofclang.minecraft.gaea.commands.InfoCommand
import org.cultofclang.minecraft.gaea.listeners.ChunkListener
import org.cultofclang.minecraft.gaea.listeners.TrackedWorld
import org.cultofclang.utils.parseJson
import org.cultofclang.utils.readAllToString

class GaeaPlugin : JavaPlugin() {
    val settings: GaeaConfig by lazy {
        getResource("config.yml")!!.readAllToString().parseJson(GaeaConfig.serializer())
    }

    private val trackedWorlds: Map<String, TrackedWorld> by lazy {
        settings.masterWorld.map { (trackedName, masterName) -> trackedName to TrackedWorld(trackedName, masterName) }
            .toMap()
    }

    fun getTrackedWorld(worldName: String): TrackedWorld? = trackedWorlds[worldName]
    fun getTrackedWorld(world: World): TrackedWorld? = getTrackedWorld(world.name)

    override fun onEnable() {
        getCommand("gaea")?.setExecutor(InfoCommand)
        server.pluginManager.registerEvents(ChunkListener, this)
        server.pluginManager.registerEvents(ProtectionListener, this)
        server.scheduler.scheduleSyncRepeatingTask(this, ChunkListener, 200, 1)

        //CustomBlockData listener
        CustomBlockData.registerListener(this)
        //logger.info(calcMarket().toString())


    }
}

