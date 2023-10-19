package org.cultofclang.minecraft.gaea

import com.mineinabyss.idofront.config.config
import com.mineinabyss.idofront.di.DI
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin
import org.cultofclang.minecraft.gaea.commands.InfoCommand

class GaeaPlugin : JavaPlugin() {

    private val trackedWorlds: Map<String, TrackedWorld> by lazy {
        gaea.config.masterWorld.map { (trackedName, masterName) -> trackedName to TrackedWorld(trackedName, masterName) }
            .toMap()
    }

    fun getTrackedWorld(worldName: String): TrackedWorld? = trackedWorlds[worldName]
    fun getTrackedWorld(world: World): TrackedWorld? = getTrackedWorld(world.name)

    override fun onEnable() {
        createGaeaContext()
        getCommand("gaea")?.setExecutor(InfoCommand)
        server.pluginManager.registerEvents(ChunkListener, this)
        server.scheduler.scheduleSyncRepeatingTask(this, ChunkListener, 200, 1)
    }

    fun createGaeaContext() {
        DI.remove<GaeaContext>()
        DI.add<GaeaContext>(object : GaeaContext {
            override val plugin: GaeaPlugin = this@GaeaPlugin
            override val config: GaeaConfig by config("config", dataFolder.toPath(), GaeaConfig())
        })
    }
}

