package org.cultofclang.utils

import java.time.Instant
import java.util.*


fun now() = Instant.now().epochSecond


val rng = Random()

fun bool(p:Float) = p > rng.nextFloat()
