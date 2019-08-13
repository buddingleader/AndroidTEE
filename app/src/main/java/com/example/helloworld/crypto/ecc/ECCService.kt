package com.example.helloworld.crypto.ecc

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.math.BigInteger
import java.security.*
import java.security.spec.ECPoint
import java.security.spec.EllipticCurve
import java.util.*
import kotlin.experimental.and

class ECCService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("on start command coming")
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
