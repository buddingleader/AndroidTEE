package com.example.helloworld.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.provider.AlarmClock
import android.widget.Toast
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.helloworld.crypto.ecc.ECCP256
import com.example.helloworld.crypto.ecc.ECCP256.publicHexForTests
import com.example.helloworld.crypto.hash.HashHelper
import com.github.kittinunf.fuel.Fuel


const val SERVER_ADDRESS = "http://192.168.127.129:8060/v1"

/**
 * A constructor is required, and must call the super IntentService(String)
 * constructor with a name for the worker thread.
 */
class SharedDataService : Service() {

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

fun upload(data: ByteArray, description: String): String? {
    var ciphertext = "android_ciphertext"
    var summary = HashHelper.sha256(data)
    var description = description
    var owner = ECCP256.publicHexForTests
    Fuel.post(
        "$SERVER_ADDRESS/tee/",
        listOf("ciphertext" to ciphertext, "summary" to summary, "description" to description, "owner" to owner)
    ).also { println(it.url) }
        .also { println(String(it.body.toByteArray())) }
        .responseString().run {
            val (result, error) = this.third
            if (error != null) {
                println("response: ${this.second}, error: $error")
                return result
            }

            return Common.parseResult(result)
        }
}


fun queryDataByID(id: String): String? {
    Fuel.get("$SERVER_ADDRESS/tee/$id").also { println(it.url) }
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

// curl -sX PUT "http://localhost:8060/v1/tee/authorize/" -H  "accept: application/json" -H  "content-type: application/x-www-form-urlencoded"
// -d "id=$notification_A&status=1&message=$A_address&encryptedKey=$encryptedKey&encryptedType=1"
fun authorizeData(notificationID: String, status: Int, message: String, encryptedType: Int): String? {
    Fuel.put(
        "$SERVER_ADDRESS/tee/authorize/",
        listOf(
            "id" to notificationID,
            "status" to status,
            "message" to message,
            "encryptedKey" to publicHexForTests,
            "encryptedType" to encryptedType
        )
    ).also { println(it.url) }
        .also { println(String(it.body.toByteArray())) }
        .responseString().run {
            val (result, error) = this.third
            if (error != null) {
                println("response: ${this.second}, error: $error")
                return result
            }

            return Common.parseResult(result)
        }
}