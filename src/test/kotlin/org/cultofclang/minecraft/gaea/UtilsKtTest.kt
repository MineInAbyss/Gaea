package org.cultofclang.minecraft.gaea

import org.cultofclang.utils.bits
import org.cultofclang.utils.sBits
import org.cultofclang.utils.setBits
import org.cultofclang.utils.setSBits
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class UtilsKtTest {

    @Test
    fun `test set bits`() {
        assertEquals(0L.setBits(0,4, 12).bits(0,4), 12)
    }

    @Test
    fun `test set signed bits`() {
        assertEquals(0L.setSBits(0,4, -3).sBits(0,4), -3)
        val z = -32
        assertEquals(0L.setSBits(34, 30, z).sBits(34, 30).toInt(),z)
    }
}