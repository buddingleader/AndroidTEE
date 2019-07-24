package com.example.helloworld.crypto.ecc

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.security.*

class ECCService : Service() {
    companion object {
        const val containerNameKey = "Android Container Name Key"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("on start command coming")
        testGenerateKeyPair(intent)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        println("on create coming")
        super.onCreate()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun testGenerateKeyPair(intent: Intent?) {
        var containerName = intent?.getStringExtra(containerNameKey) ?: containerNameKey
        var eccP256 = ECCP256()
        var keypair = eccP256.generateP256KeyPair(containerName)

        // verify keypair
        val data = "Hello World!".toByteArray()
        var signature = eccP256.sign(keypair.private, data)
        var verified = eccP256.verify(keypair.public, data, signature)
        println("containerName: $containerName")
        println("keypair: $keypair")
        println("data: $data")
        var hexSign = signature?.joinToString("") { "%02x".format(it) }
        println("signature: $hexSign")
        println("verified: $verified")
    }
}
