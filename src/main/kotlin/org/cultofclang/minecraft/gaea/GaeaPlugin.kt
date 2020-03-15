package org.cultofclang.minecraft.gaea

import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.plugin.java.JavaPlugin
import org.cultofclang.minecraft.gaea.commands.InfoCommand
import org.cultofclang.utils.bool
import org.cultofclang.utils.parseJson
import org.cultofclang.utils.readAllToString
import org.cultofclang.utils.zipZoneBlocks
import kotlin.system.measureNanoTime

class GaeaPlugin : JavaPlugin(){
    val settings:GaeaConfig by lazy { getResource("config.json")!!.readAllToString().parseJson(GaeaConfig.serializer()) }

    private val trackedWorlds:Map<String, TrackedWorld> by lazy {
        settings.masterWorld.map { (trackedName, masterName)  ->  trackedName to TrackedWorld(trackedName, masterName)}.toMap()
    }

    fun getTrackedWorld(worldName: String) : TrackedWorld? = trackedWorlds[worldName]
    fun getTrackedWorld(world: World) : TrackedWorld? = getTrackedWorld(world.name)




    fun isWorldVersioned(world: World):Boolean = settings.masterWorld.containsKey(world.name)

    override fun onEnable() {
        getCommand("gaea")?.setExecutor(InfoCommand)
        server.pluginManager.registerEvents(ChunkListener, this)

        server.scheduler.scheduleSyncRepeatingTask(this, ChunkListener,200,1)
    }


}

