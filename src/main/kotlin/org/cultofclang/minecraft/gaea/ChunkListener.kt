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

object ChunkListener : Listener, Runnable {

    private var toProcess = ConcurrentLinkedQueue<Chunk>()

    fun addBalance(location: Location, change: Float) {
        val zone = Zone.get(location) ?: return
        zone.markDirtyAndSetMinBalance(change)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun BlockBreakEvent.onBlockPlace() {
        addBalance(block.location, gaea.config.decayTimeBlockBreak)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun BlockPlaceEvent.onBlockBreak() {
        addBalance(block.location, gaea.config.decayTimeBlockPlace)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun ChunkLoadEvent.onChunkLoad() {
        toProcess.add(chunk)
    }

    override fun run() {
        var done = 0
        while (done < gaea.config.maxDecayPerTick) {
            val chunk = toProcess.poll() ?: return
            if (!chunk.isLoaded)
                continue

            for (y in 0 until CHUNK_HEIGHT step ZONE_SIZE)
                if (Zone.getweak(chunk.getBlock(0, y, 0).location)?.decay() == true)
                    done += 1
        }
    }
}
