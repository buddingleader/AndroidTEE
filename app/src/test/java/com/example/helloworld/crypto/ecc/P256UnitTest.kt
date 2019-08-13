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
    fun parsePublicKey(){
        val keypair = ECCP256.generateKeyPair()

        val serverPubKey =
            "04deb43a5bb4c34cf8db53311d4d9f95d2356b8c011349ecb04fc00b73c303bc9dc0675f4ca45a562f589b993a94129482eb9b03f259ce8982e525927c3f70fdbe"
        val ecPubKey = ECCP256.fromPublicHex(serverPubKey)
        val aesKey = ECDH.generateSharedSecret(keypair.private as ECPrivateKey, ecPubKey)
        println("aesKey:${aesKey.contentToString()}")
        println("aesKeyHex:${HexUtil.bytesToHex(aesKey)}")

//        val data = "Hello World!".toByteArray()
//        println("data:${data.contentToString()}")
//        val ciphertext = ECDH.encrypt(secretKey, data)
//        println("ciphertext:$ciphertext")

//        val keySpec = X509EncodedKeySpec(keypair.public.encoded)
//        val keyFactory = KeyFactory.getInstance("EC")
//        val pubKey = keyFactory.generatePublic(keySpec)
//        val data = "hello world!".toByteArray()
//        val signature = sign(keypair.private, data)
//        val verified = verify(pubKey, data, signature)
//        Assert.assertTrue(verified)
    }
}