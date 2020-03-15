package org.cultofclang.utils

import org.bukkit.Chunk
import org.bukkit.block.Block


const val CHUNK_HEIGHT: Int = 256
const val CHUNK_WIDTH:Int = 16
const val ZONE_SIZE:Int = 16

fun chunk(block: Int):Int{
    return block shr 4
}

fun allChunkOffsets() = sequence {
    for(y in 0 until CHUNK_HEIGHT){
        for(x in 0 until CHUNK_WIDTH){
            for(z in 0 until CHUNK_WIDTH){
                yield(Triple(x,y,z))
            }
        }
    }
}

inline fun zipZoneBlocks(a:Chunk, b:Chunk, yZone:Int, action:(Block, Block) -> Unit){
    val startY = yZone * ZONE_SIZE
    val endY = startY + ZONE_SIZE
    for(y in startY until endY) {
        for (x in 0 until CHUNK_WIDTH) {
            for (z in 0 until CHUNK_WIDTH) {
                action(a.getBlock(x,y,z), b.getBlock(x,y,z))
            }
        }
    }
}
inline fun zipChunkBlocks(a:Chunk, b:Chunk, action:(Block, Block) -> Unit){
    for(y in 0 until CHUNK_HEIGHT) {
        for (x in 0 until CHUNK_WIDTH) {
            for (z in 0 until CHUNK_WIDTH) {
                action(a.getBlock(x,y,z), b.getBlock(x,y,z))
            }
        }
    }
}

inline fun Chunk.forEachBlock(action:(Block) -> Unit){
    for(y in 0 until CHUNK_HEIGHT){
        for(x in 0 until CHUNK_WIDTH){
            for(z in 0 until CHUNK_WIDTH){
                action(getBlock(x,y,z))
            }
        }
    }
}



