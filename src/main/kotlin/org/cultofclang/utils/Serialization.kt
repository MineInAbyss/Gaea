package org.cultofclang.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.BufferedReader
import java.io.InputStream

val JsonStable = Json(JsonConfiguration.Stable)

fun InputStream.readAllToString() = this.bufferedReader().use(BufferedReader::readText)

fun <T>String.parseJson(serializer: KSerializer<T>):T = JsonStable.parse(serializer,this)