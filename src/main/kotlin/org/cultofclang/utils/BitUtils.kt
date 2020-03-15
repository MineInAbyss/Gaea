package org.cultofclang.utils

fun lMask(len:Int) = (1L shl len) -1
fun mid(len:Int) = (1 shl (len -1))

fun Long.bits(from:Int, length:Int):Long{
    return ((this shr from) and lMask(length))
}

fun Long.setBits(from:Int, length:Int, value:Int):Long{
    val meow = lMask(length) and value.toLong()
    return this or (meow shl from)
}


fun Long.sBits(from:Int, length:Int):Long{
    return this.bits(from, length) - mid(length)
}

fun Long.setSBits(from:Int, length:Int, value:Int):Long{
    return this.setBits(from, length, value + mid(length))
}