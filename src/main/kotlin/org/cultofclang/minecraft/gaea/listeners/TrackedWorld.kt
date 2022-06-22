package org.cultofclang.minecraft.gaea.listeners

import org.bukkit.Bukkit
import org.bukkit.World
import org.cultofclang.minecraft.gaea.Zones
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionManager
import java.sql.Connection

class TrackedWorld(val world: World, val master: World, var database: Database) {

    companion object {
        private fun setUpDb(worldName: String): Database {
            val db = Database.connect("jdbc:sqlite:$worldName.db", "org.sqlite.JDBC")
            db.transactionManager.defaultIsolationLevel =
                Connection.TRANSACTION_SERIALIZABLE //TRANSACTION_READ_UNCOMMITTED

            transaction(db) {
                SchemaUtils.createMissingTablesAndColumns(Zones)
            }
            return db
        }
    }

    constructor(trackedName: String, masterName: String) : this(
        world = Bukkit.getServer().getWorld(trackedName) ?: error("cannot load world $masterName"),
        master = Bukkit.getServer().getWorld(masterName) ?: error("cannot load master world $masterName"),
        database = setUpDb(trackedName)
    )
}
