package org.cultofclang.minecraft.gaea

import com.jeff_media.customblockdata.CustomBlockData
import com.jeff_media.customblockdata.events.CustomBlockDataMoveEvent
import com.jeff_media.customblockdata.events.CustomBlockDataRemoveEvent
import com.jeff_media.morepersistentdatatypes.DataType
import com.mineinabyss.blocky.helpers.getGearyEntityFromBlock
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.cultofclang.minecraft.gaea.components.RegenProtection

object ProtectionListener : Listener {

    //TODO Make this fire when "activating" the protblock not just on placing it
    // Get all blocks within radius and make them "protected"
    @EventHandler
    fun BlockPlaceEvent.onPlaceProtectionBlock() {
        block.getGearyEntityFromBlock()?.get<RegenProtection>() ?: return
        blockPlaced.addBlockProtection()
    }

    // Remove protection from blocks when protblock is removed
    @EventHandler
    fun CustomBlockDataRemoveEvent.onProtectionBlockRemoved() {
        block.getGearyEntityFromBlock()?.get<RegenProtection>() ?: return
        block.removeBlockProtection()
    }

    // Get all blocks within radius of moved block and protect them
    @EventHandler
    fun CustomBlockDataMoveEvent.onProtectionBlockMoved() {
        block.getGearyEntityFromBlock()?.get<RegenProtection>() ?: return
        block.addBlockProtection()
    }

    // Adds blockprotection to this block
    private fun Block.addBlockProtection() {
        val rad = getGearyEntityFromBlock()?.get<RegenProtection>()?.protectionRadius ?: return
        val loc = location.clone()

        for (x in (loc.blockX - rad..loc.blockX + rad)) {
            for (y in (loc.blockY - rad..loc.blockY + rad)) {
                for (z in (loc.blockZ - rad..loc.blockZ + rad)) {
                    val pdc = CustomBlockData(world.getBlockAt(x, y, z), Gaea)

                    // If it already has another blockprotection, add to array
                    if (pdc.has(ProtectionKey)) {
                        val array = pdc.get(ProtectionKey, DataType.LOCATION_ARRAY)
                        array?.get(array.lastIndex)?.add(location)
                    } else {
                        // Otherwise set the key and add originloc
                        pdc.set(ProtectionKey, DataType.LOCATION_ARRAY, arrayOf(location))
                    }
                }
            }
        }
    }

    // Removes a specific protectionblock from this block
    private fun Block.removeBlockProtection() {
        val rad = getGearyEntityFromBlock()?.get<RegenProtection>()?.protectionRadius ?: return
        val loc = location.clone()

        for (x in (loc.blockX - rad..loc.blockX + rad)) {
            for (y in (loc.blockY - rad..loc.blockY + rad)) {
                for (z in (loc.blockZ - rad..loc.blockZ + rad)) {
                    val b = world.getBlockAt(x, y, z)
                    val pdc = CustomBlockData(b, Gaea)
                    val locArray = pdc.get(ProtectionKey, DataType.LOCATION_ARRAY) ?: return
                    if (locArray.contains(location)) {
                        val newArray = locArray.filter { it != location }.toTypedArray()
                        if (newArray.isNotEmpty()) pdc.set(ProtectionKey, DataType.LOCATION_ARRAY, newArray)
                        else pdc.remove(ProtectionKey)
                    }
                }
            }
        }
    }

    // Removes all protections from this block
    private fun Block.clearBlockProtections() {
        val rad = getGearyEntityFromBlock()?.get<RegenProtection>()?.protectionRadius ?: return
        val loc = location.clone()

        for (x in (loc.blockX - rad..loc.blockX + rad))
            for (y in (loc.blockY - rad..loc.blockY + rad))
                for (z in (loc.blockZ - rad..loc.blockZ + rad))
                    CustomBlockData(world.getBlockAt(x, y, z), Gaea).remove(ProtectionKey)
    }
}
