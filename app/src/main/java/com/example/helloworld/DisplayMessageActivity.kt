package com.example.helloworld

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.TextView
import android.widget.Toast
import com.example.helloworld.crypto.aes.AESHelper
import com.example.helloworld.crypto.hash.HashHelper
import java.io.File

class DisplayMessageActivity : AppCompatActivity() {
    companion object {
        const val AES_SECRET_KEY: String = "AES_SECRET_KEY"
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
        val outStream = openFileOutput("${HashHelper.sha256(message)}.txt", Context.MODE_PRIVATE)
        outStream.write(encryptedHex.toByteArray(Charsets.UTF_8))
        outStream.close()

        val dir = File("")
        val fileTree = dir.walk()
        fileTree.maxDepth(1).forEach(::println)

        val text = "Hello user!"
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
        Toast.makeText(this, "Successfully to encrypt!", Toast.LENGTH_SHORT).show()
    }
}
