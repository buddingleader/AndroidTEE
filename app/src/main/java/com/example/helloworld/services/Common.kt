package com.example.helloworld.services

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser

object Common {
    fun parseResult(result: String?): String? {
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

    //    fun parseNotifications(task: String?): List<String> {
    fun parseNotifications(task: String?): Map<String, Any?>? {
        val parser: Parser = Parser.default()
        val stringBuilder: StringBuilder = StringBuilder(task.toString())
        val json: JsonObject = parser.parse(stringBuilder) as JsonObject

        val (target, success) = Pair(json.obj("result"), json.boolean("success"))
        when (success) {
            true -> {
                println("target:$target")
                val result = target?.obj("dataNotifications")
                return result?.toMap()
            }
            else -> {
                println("error: $task")
            }
        }

        return HashMap()
    }

    fun parseRequester(task: String?): String? {
        val parser: Parser = Parser.default()
        val stringBuilder: StringBuilder = StringBuilder(task.toString())
        val json: JsonObject = parser.parse(stringBuilder) as JsonObject

        val (target, success) = Pair(json.obj("result"), json.boolean("success"))
        when (success) {
            true -> {
                println("target:$target")
                return target?.string("requester")
            }
            else -> {
                println("error: $task")
            }
        }

        return task
    }
}