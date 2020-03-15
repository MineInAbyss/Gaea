package org.cultofclang.minecraft.gaea

import org.jetbrains.exposed.dao.id.LongIdTable

object Zones : LongIdTable() {
    val balance = float("balance")
    val timestamp = long("timestamp")
}




