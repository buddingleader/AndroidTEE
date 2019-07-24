package com.example.helloworld

import android.content.Intent
import android.app.Service
import android.content.Context
import android.os.IBinder
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.Toast


/**
 * A constructor is required, and must call the super IntentService(String)
 * constructor with a name for the worker thread.
 */
class ContainerService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("on start command coming")

        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        var dataPath = filesDir.absolutePath
        val outStream = openFileOutput("a.txt", Context.MODE_PRIVATE)
        outStream.write("hello world!".toByteArray())
        outStream.write(intent!!.getStringExtra(EXTRA_MESSAGE).toByteArray())
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
        return null
    }
}
