package org.cultofclang.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ChunkKtTest {

    @Test
    fun `chunk from location`() {
        assertEquals(chunk(5),0)
        assertEquals(chunk(-1),-1)
    }
}