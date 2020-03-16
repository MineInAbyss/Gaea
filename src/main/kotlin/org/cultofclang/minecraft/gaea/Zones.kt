package org.cultofclang.minecraft.gaea

import org.jetbrains.exposed.dao.id.LongIdTable

object Zones : LongIdTable() {
    val balance = float("balance").default(0f)
    val timestamp = long("timestamp").default(0L)
    val dirty = bool("dirty").default(false)
}




