package org.cultofclang.minecraft.gaea

import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.cultofclang.utils.*
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.pow
import kotlin.system.measureNanoTime

class Zone(id: EntityID<Long>) : LongEntity(id) {
    val changed: Boolean get() = dirty
    lateinit var chunk: Chunk
    lateinit var world: TrackedWorld

    companion object : LongEntityClass<Zone>(Zones) {
        private fun posIndex(x: Int, y: Int, z: Int) = 0L
            .setSBits(4, 30, x)
            .setBits(0, 4, y)
            .setSBits(34, 30, z)


        fun getweak(location: Location): Zone? {
            val x = chunk(location.blockX)
            val y = chunk(location.blockY)
            val z = chunk(location.blockZ)
            val world = gaea.plugin.getTrackedWorld(location.world ?: return null) ?: return null

            var output: Zone? = null

            transaction(world.database) {
                output = findById(posIndex(x, y, z))
            }

            if (output != null) {
                output!!.world = world
                output!!.chunk = location.chunk
            }
            return output
        }

        fun get(location: Location): Zone? {
            val x = chunk(location.blockX)
            val y = chunk(location.blockY)
            val z = chunk(location.blockZ)
            val world = gaea.plugin.getTrackedWorld(location.world ?: return null) ?: return null

            lateinit var output: Zone
            transaction(world.database) {
                output = findById(posIndex(x, y, z))
                    ?: Zone.new(posIndex(x, y, z)) {
                        balance = 0f
                        timestamp = now()
                    }
            }
            output.world = world
            output.chunk = location.chunk
            return output
        }
    }

    private var balance by Zones.balance
    private var timestamp by Zones.timestamp
    private var dirty by Zones.dirty

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

    val effectiveBalance: Float get() = balance - timePassed

    val timePassed get() = now() - timestamp

    fun set(value: Float) {
        transaction(world.database) {
            balance = value
        }
    }

    private fun update(value: Float, setClean: Boolean) {
        transaction(world.database) {
            timestamp = now()
            balance = value.coerceAtLeast(effectiveBalance)
            if (setClean)
                dirty = false
        }
    }

    fun markDirtyAndSetMinBalance(minEffectiveBal: Float, setDirty: Boolean = true) {
        transaction(world.database) {
            balance = (effectiveBalance.coerceAtLeast(minEffectiveBal)) + timePassed
            if (setDirty)
                dirty = true
        }
    }

    fun decay(force: Boolean = false): Boolean {
        val zone = this
        val chunk = zone.chunk

        val shouldRegen = zone.dirty && zone.effectiveBalance < 0
        if (shouldRegen || force) {

            var changesToBeMade = false
            val timeMs = measureNanoTime {
                val masterChunk = zone.world.master.getChunkAt(chunk.x, chunk.z)

                val decayPower = (1 - zone.effectiveBalance / gaea.config.timeBetweenDecay).coerceAtLeast(1f)

                zipZoneBlocks(chunk, masterChunk, zone.y) { client: Block, master: Block ->
                    val prob = gaea.config.getDecayProbability(client.type)

                    if (client.type != master.type) {
                        changesToBeMade = true
                        val realProb = 1 - (1 - prob).pow(decayPower)
                        if (bool(realProb)) {
                            client.setBlockData(master.blockData, false)

                            if (master.type == Material.PLAYER_HEAD) {
                                Bukkit.getLogger().info("Found a skull ${client.location}")
                            }
                        }
                    }
                }
            }
            //if(!force)
            zone.update(0f, setClean = !changesToBeMade)
            Bukkit.getLogger().info("apply decay for $zone in ${timeMs / 1.0e6}ms")
            return true
        }
        return false
    }

    fun claim(sender: Player, claimTime: Float) {
        Bukkit.getLogger().info("$sender claim $this for ${durationHuman(claimTime)}")
        update(claimTime, setClean = false)
    }

    override fun toString(): String {
        return "Zone[$x,$y,$z]"
    }
}

