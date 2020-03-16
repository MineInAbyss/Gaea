package org.cultofclang.utils

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.KSerializer
import java.io.BufferedReader
import java.io.InputStream

fun InputStream.readAllToString() = this.bufferedReader().use(BufferedReader::readText)

fun <T>String.parseJson(serializer: KSerializer<T>):T = Yaml.default.parse(serializer,this)