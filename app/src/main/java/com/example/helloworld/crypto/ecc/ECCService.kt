package com.example.helloworld.crypto.ecc

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import java.math.BigInteger
import java.security.*
import java.security.interfaces.ECPrivateKey
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

    val PRIV_FILE = "priv.pem"
    val PUB_FILE = "priv.pem"
    fun storeKeyPair(keyPair: Pair<ByteArray, ByteArray>) {
        val privPemContents = ECC.toPEMFileContents(keyPair.first)
        val privOutStream = openFileOutput(PRIV_FILE, Context.MODE_PRIVATE)
        privOutStream.write(privPemContents)
        privOutStream.close()

        val pubPemContents = ECC.toPEMFileContents(keyPair.second)
        val pubOutStream = openFileOutput(PUB_FILE, Context.MODE_PRIVATE)
        pubOutStream.write(pubPemContents)
        pubOutStream.close()
    }
}
