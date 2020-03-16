package org.cultofclang.utils

import java.time.Duration
import java.time.Instant
import java.util.*

fun now() = Instant.now().epochSecond


val rng = Random()

fun bool(p:Float) = p > rng.nextFloat()


fun durationHuman(timeSecs:Float):String{
    val dur = Duration.ofSeconds(timeSecs.toLong())!!

    val d = dur.toDays()
    val h = dur.toHours()
    val m = dur.toMinutes()
    val s = dur.toSeconds()

    if(d>0)
        return "$d days"

    if(h>0)
        return "$h hours"

    if(m>0)
        return "$m min"

    if(s>0)
        return "$h seconds"

    return "now"
}