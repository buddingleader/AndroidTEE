package com.example.helloworld

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.TextView
import android.widget.Toast
import com.example.helloworld.crypto.aes.AESHelper
import com.example.helloworld.crypto.hash.HashHelper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import java.io.File

class DisplayMessageActivity : AppCompatActivity() {
    companion object {
        const val AES_SECRET_KEY: String = "AES_SECRET_KEY"
        const val SERVER_ADDRESS = "http://192.168.127.129:12666/api"


        fun downloadFile(pathname: String): String? {
            Fuel.download("$SERVER_ADDRESS/files/$pathname")
                .fileDestination { _, _ -> File.createTempFile("temp", ".tmp") }
                .progress { readBytes, totalBytes ->
                    val progress = readBytes.toFloat() / totalBytes.toFloat() * 100
                    println("$pathname downloaded $readBytes / $totalBytes ($progress %)")
                }
                .responseString().run {
                    val (result, error) = this.third
                    if (error != null) {
                        println("download file[$pathname] error: $error")
                    }

                    return result
                }
        }

        // pathname: lipsum.txt
        fun uploadFile(pathname: String): String? {
            Fuel.upload("$SERVER_ADDRESS/files")
                .add { FileDataPart(File(pathname), name = "file", filename = pathname) }
                .responseString().run {
                    val (result, error) = this.third
                    if (error != null) {
                        println("upload file[$pathname] error: $error")
                    }

                    return result?.split("file_id: ")?.get(1)
                }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message)

        // Get the Intent that started this activity and extract the string
        val message = intent.getStringExtra(EXTRA_MESSAGE)
        val aesString = intent.getStringExtra(AES_SECRET_KEY)
        val encryptedHex = AESHelper.encrypt(message, aesString)

        // Capture the layout's TextView and set the string as its text
        findViewById<TextView>(R.id.textView1).apply {
            text = message
        }
        findViewById<TextView>(R.id.textView3).apply {
            text = encryptedHex
        }

        // Write encrypted hex to a file, named message hash, and the type is text
        val filename = HashHelper.sha256(message)
        val outStream = openFileOutput(filename, Context.MODE_PRIVATE)
        outStream.write(encryptedHex.toByteArray(Charsets.UTF_8))
        outStream.close()

        // Ping the server
        Fuel.get("$SERVER_ADDRESS/ping")
            .response { request, response, result ->
                println(request)
                println(response)
                val (bytes, error) = result
                if (error != null) {
                    println("error: $error")
                }
                if (bytes != null) {
                    println("[response bytes] ${String(bytes)}")
                }
            }
        // Upload the file to server
        val filepath = "$filesDir/$filename"
        val fileID=uploadFile(filepath)
        val data=downloadFile(fileID.toString())
        if (data != encryptedHex){
            println("upload/download error, downloaded data: $data, encryptedHex: $encryptedHex")
        }

        val text = "Hello user!"
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
        Toast.makeText(this, "Successfully to encrypt!", Toast.LENGTH_SHORT).show()
    }
}