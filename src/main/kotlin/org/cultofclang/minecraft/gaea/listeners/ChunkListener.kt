package org.cultofclang.minecraft.gaea.listeners

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.inventory.ItemStack
import org.cultofclang.minecraft.gaea.Gaea
import org.cultofclang.minecraft.gaea.Zone
import org.cultofclang.utils.CHUNK_HEIGHT
import org.cultofclang.utils.ZONE_SIZE
import java.util.concurrent.ConcurrentLinkedQueue

object ChunkListener : Listener, Runnable {


    @EventHandler(priority = EventPriority.HIGHEST)
    fun PlayerInteractEvent.onPlayerInteract() {

        if (clickedBlock?.type == Material.EMERALD_BLOCK) {
            player.sendMessage("You found me!")
            isCancelled = true
            val inv = Bukkit.getServer().createInventory(player, 54, Component.text("Void Box"))
            inv.setItem(0, ItemStack(Material.CLAY_BALL, 500))
            inv.setItem(1, ItemStack(Material.OAK_BOAT))
            inv.setItem(1, ItemStack(Material.DIAMOND_HOE))
            player.openInventory(inv)
        }
    }

    private var toProcess = ConcurrentLinkedQueue<Chunk>()

    private fun addBalance(location: Location, change: Float) {
        val zone = Zone.get(location) ?: return
        zone.markDirtyAndSetMinBalance(change)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun BlockBreakEvent.onBlockPlace() {
        addBalance(block.location, Gaea.settings.decayTimeBlockBreak)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun BlockPlaceEvent.onBlockBreak() {
        addBalance(block.location, Gaea.settings.decayTimeBlockPlace)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun ChunkLoadEvent.onChunkLoad() {
        toProcess.add(chunk)
    }

    override fun run() {
        var done = 0
        while (done < Gaea.settings.maxDecayPerTick) {
            val chunk = toProcess.poll() ?: return
            if (!chunk.isLoaded)
                continue

            for (y in 0 until CHUNK_HEIGHT step ZONE_SIZE)
                if (Zone.getweak(chunk.getBlock(0, y, 0).location)?.decay() == true)
                    done += 1
        }
    }
}
