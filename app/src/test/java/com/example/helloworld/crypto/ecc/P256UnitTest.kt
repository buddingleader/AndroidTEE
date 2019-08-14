package com.example.helloworld.crypto.ecc

import com.example.helloworld.utils.HexUtil
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECPublicKeySpec
import java.util.*

class P256UnitTest {
    @Test
    fun generateKeyPair_isCorrect() {
        val keypair = ECCP256.generateKeyPair()

        val data = "Hello World!".toByteArray()
        var signature = ECCP256.sign(keypair.private, data)
        var verified = ECCP256.verify(keypair.public, data, signature)
        Assert.assertTrue(verified)
    }

    @Test
    fun parsePublicKey() {
        // x/y bytes.size = 33
        var serverPubKey =
            "04deb43a5bb4c34cf8db53311d4d9f95d2356b8c011349ecb04fc00b73c303bc9dc0675f4ca45a562f589b993a94129482eb9b03f259ce8982e525927c3f70fdbe"
        var ecPubKey = ECCP256.fromPublicHex(serverPubKey)
        var ecPubHex = ECCP256.toPublicHex(ecPubKey)
        Assert.assertEquals(serverPubKey, ecPubHex)

        // Repeated Check - x/y bytes.size = 32
        serverPubKey =
            "04747ee4c19b26e5c36b23674dd0d3b0d3a37935200450e25ca78a90a360e7afc813419188a6d9e6b5dd3bfe2a0970ebd659288255b472d8f16d258716e6a29f40"
        ecPubKey = ECCP256.fromPublicHex(serverPubKey)
        ecPubHex = ECCP256.toPublicHex(ecPubKey)
        Assert.assertEquals(serverPubKey, ecPubHex)

        // Error - uncompressed point error
        serverPubKey =
            "03747ee4c19b26e5c36b23674dd0d3b0d3a37935200450e25ca78a90a360e7afc813419188a6d9e6b5dd3bfe2a0970ebd659288255b472d8f16d258716e6a29f40"
        ecPubKey = ECCP256.fromPublicHex(serverPubKey)
        Assert.assertEquals(ecPubKey.w, ECC.ERROR_EC_POINT)

        // Error - invalid public key
        serverPubKey =
            "04847ee4c19b26e5c36b23674dd0d3b0d3a37935200450e25ca78a90a360e7afc813419188a6d9e6b5dd3bfe2a0970ebd659288255b472d8f16d258716e6a29f40"
        ecPubKey = ECCP256.fromPublicHex(serverPubKey)
        Assert.assertEquals(ecPubKey.w, ECC.ERROR_EC_POINT)
    }


    @Test
    fun formatPublicKey() {
        var clientPubKey = ECCP256.generateKeyPair().public as ECPublicKey
        var ecPubHex = ECCP256.toPublicHex(clientPubKey)
        var ecPubKey = ECCP256.fromPublicHex(ecPubHex)
        Assert.assertEquals(clientPubKey, ecPubKey)

        // Repeat check ten times
        for (count in 1..10) {
            clientPubKey = ECCP256.generateKeyPair().public as ECPublicKey
            ecPubHex = ECCP256.toPublicHex(clientPubKey)
            ecPubKey = ECCP256.fromPublicHex(ecPubHex)
            Assert.assertEquals(clientPubKey, ecPubKey)
        }
    }
}