package com.example.helloworld.services

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser

object Common {
    fun parseResult(result: String?): String? {
        // parse response body result
        val parser: Parser = Parser.default()
        val stringBuilder: StringBuilder = StringBuilder(result.toString())
        val json: JsonObject = parser.parse(stringBuilder) as JsonObject

        val (target, success) = Pair(json.string("result"), json.boolean("success"))
        when (success) {
            true -> {
                return target
            }
            else -> {
                println("error: $result")
            }
        }

        return result
    }
}