package com.example.helloworld

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.TextView
import android.widget.Toast
import com.example.helloworld.crypto.aes.AESHelper

class DisplayMessageActivity : AppCompatActivity() {
    companion object{
        const val AES_SECRET_KEY:String = "AES_SECRET_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message)

        // Get the Intent that started this activity and extract the string
        val message = intent.getStringExtra(EXTRA_MESSAGE)
        val aesString = intent.getStringExtra(AES_SECRET_KEY)

        // Capture the layout's TextView and set the string as its text
        findViewById<TextView>(R.id.textView1).apply {
            text = message
        }
        findViewById<TextView>(R.id.textView3).apply {
            text = AESHelper.encrypt(message,aesString)
        }

//        println("start container service")
//        startService(Intent(this, ContainerService::class.java).apply {
//            putExtra(EXTRA_MESSAGE, message)
//        })
//        startService(Intent(this, ECCService::class.java).apply {
//            putExtra(ECCService.containerNameKey, message)
//        })
//        println("end container service")

        val text = "Hello user!"
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
        Toast.makeText(this, "Successfully to encrypt!", Toast.LENGTH_SHORT).show()
    }
}
