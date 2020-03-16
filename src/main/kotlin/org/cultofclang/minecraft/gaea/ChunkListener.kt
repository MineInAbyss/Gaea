package org.cultofclang.minecraft.gaea

import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.world.ChunkLoadEvent

import org.cultofclang.utils.CHUNK_HEIGHT
import org.cultofclang.utils.ZONE_SIZE
import java.util.concurrent.ConcurrentLinkedQueue

object ChunkListener : Listener , Runnable {

    private var toProcess = ConcurrentLinkedQueue<Chunk>()

    fun addBalance(location: Location, change: Float){
        val zone = Zone.get(location)?:return
        zone.markDirtyAndSetMinBalance(change)
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockPlace(event: BlockBreakEvent){
        addBalance(event.block.location, Gaea.settings.decayTimeBlockBreak)
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreak(event: BlockPlaceEvent){
        addBalance(event.block.location, Gaea.settings.decayTimeBlockPlace)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onChunkLoad(event: ChunkLoadEvent){
        val chunk = event.chunk
        toProcess.add(chunk)
    }

    override fun run() {
        var done = 0
        while(done < Gaea.settings.maxDecayPerTick) {
            val chunk = toProcess.poll() ?: return
            if(!chunk.isLoaded)
                continue
            Gaea.getTrackedWorld(chunk.world) ?: continue
            for (y in 0 until CHUNK_HEIGHT step ZONE_SIZE)
                if(Zone.get(chunk.getBlock(0, y, 0).location)!!.decay())
                    done+=1
        }
    }
}