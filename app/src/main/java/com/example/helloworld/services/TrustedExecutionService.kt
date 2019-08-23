package com.example.helloworld.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.provider.AlarmClock
import android.widget.Toast
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.helloworld.crypto.ecc.ECCP256.publicHexForTests
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.DataPart
import com.github.kittinunf.fuel.core.Method

/**
 * A constructor is required, and must call the super IntentService(String)
 * constructor with a name for the worker thread.
 */
class TrustedExecutionService : Service() {
    companion object {
        const val SERVER_ADDRESS = "http://192.168.127.129:8060/v1"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("on start command coming")

        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        val outStream = openFileOutput("a.txt", Context.MODE_PRIVATE)
        outStream.write("hello world!".toByteArray())
        outStream.write(intent!!.getStringExtra(AlarmClock.EXTRA_MESSAGE).toByteArray())
        outStream.close()

        val text = "Hello toast!"
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
        println("end to createContainer")
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        println("on create coming")
        super.onCreate()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

fun createExecutionTask(dataIDs: List<String>, algorithmID: String, resultAddress: String): String? {
    var buffer = StringBuffer()
    for ((index, dataID) in dataIDs.withIndex()) {
        buffer.append(dataID)
        if (index != dataIDs.size - 1) buffer.append(",")
    }
    Fuel.post(
        "$SERVER_ADDRESS/task/",
        listOf("data_id[]" to buffer.toString(), "algorithm_id" to algorithmID, "result_address" to resultAddress)
    ).also { println(it.url) }
        .responseString().run {
            val (result, error) = this.third
            if (error != null) {
                println("response: ${this.second}, error: $error")
                return result
            }

            return Common.parseResult(result)
        }
}

fun queryTaskByID(taskID: String): String? {
    Fuel.get("$SERVER_ADDRESS/task/$taskID").also { println(it.url) }
        .also { println(String(it.body.toByteArray())) }
        .responseString().run {
            //            println("resp: ${this.second}")
            val (result, error) = this.third
            if (error != null) {
                println("error: $error")
            }

            return result
        }
}

//curl -sX PUT "http://localhost:8060/v1/task/" -F "algorithm_file=@/home/rabbit/teetest/client/resume" -F "task_id=$task_id" -F "executor=$A_owner" -F "container_type=1"
fun executeTask(taskID: String, containerType: Int): String? {
    Fuel.upload(
        "$SERVER_ADDRESS/task/",Method.PUT,
        listOf("task_id" to taskID, "executor" to publicHexForTests, "container_type" to containerType)
    )
        .also { println(it.url) }
        .responseString().run {
            val (result, error) = this.third
            if (error != null) {
                println("response: ${this.second}, error: $error")
                return result
            }

            return Common.parseResult(result)
        }
}