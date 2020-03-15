package org.cultofclang.minecraft.gaea

import org.bukkit.Chunk
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.cultofclang.utils.CHUNK_HEIGHT
import java.util.concurrent.ConcurrentLinkedQueue

object ChunkListener : Listener , Runnable {

    private var toProcess = ConcurrentLinkedQueue<Chunk>()

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onChunkLoad(event: ChunkLoadEvent){
        val chunk = event.chunk
        toProcess.add(chunk)
    }

    override fun run() {
        for (i in 1..Gaea.settings.maxDecayPerTick) {
            val chunk = toProcess.poll() ?: return
            if(!chunk.isLoaded)
                return
            Gaea.getTrackedWorld(chunk.world) ?: return
            for (y in 0 until CHUNK_HEIGHT step 16)
                decay(Zone.get(chunk.getBlock(0, y, 0).location)!!)
        }
    }
}