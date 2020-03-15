package org.cultofclang.minecraft.gaea

import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.cultofclang.utils.*
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.system.measureNanoTime

class Zone(id: EntityID<Long>) : LongEntity(id) {
    lateinit var chunk: Chunk
    lateinit var world:TrackedWorld
    companion object : LongEntityClass<Zone>(Zones){
        private fun posIndex(x:Int, y:Int, z:Int) = 0L
                .setSBits(4,30, x)
                .setBits(0,4, y)
                .setSBits(34, 30, z)

        fun get(location: Location): Zone? {
            val x = chunk(location.blockX)
            val y = chunk(location.blockY)
            val z = chunk(location.blockZ)
            val world = Gaea.getTrackedWorld(location.world?: return null)?:return null

            lateinit var output: Zone
            transaction(world.database) {
                output = findById(posIndex(x, y, z))
                    ?:Zone.new(posIndex(x, y, z)){
                        balance = 0f
                        timestamp = now()
                    }
            }
            output.world = world
            output.chunk = location.chunk
            return output
        }
    }
    var  balance by Zones.balance
    var  timestamp by Zones.timestamp

    val y: Int
        get() {
            return id.value.bits(0, 4).toInt()
        }

    val x: Int
        get() {
            return id.value.sBits(4, 30).toInt()
        }


    val z: Int
        get() {
            return id.value.sBits(34, 30).toInt()
        }



    val timePassed get() = now() - timestamp

    fun  update(value: Float){
        transaction(world.database) {
            balance = value
            timestamp = now()
        }
    }
}

fun decay(zone: Zone, force: Boolean = false) {
    val chunk = zone.chunk

    val shouldRegen =zone.timePassed - zone.balance > Gaea.settings.decayTimeSecs
    if(shouldRegen || force){
        val timeMs =   measureNanoTime {
            val masterChunk = zone.world.master.getChunkAt(chunk.x, chunk.z)


            zipZoneBlocks(chunk, masterChunk, zone.y) { client: Block, master: Block ->
                val prob = Gaea.settings.getDecayProbability(client.type)

                //&& client.type != master.type
                if (bool(prob) ) {
                    client.setBlockData( master.blockData,false)

                    if(master.type == Material.PLAYER_HEAD)
                    {
                        Bukkit.getLogger().info("Found a skull ${client.location}")
                    }

                    //todo add support for signs + heads + banners
                }
            }
        }
        zone.update(0f)
        Bukkit.getLogger().info("apply decay for zone ${zone.x} ${zone.y} ${zone.z} chunk ${chunk.x} ${chunk.z} in ${timeMs/1.0e6}ms")
    }
}