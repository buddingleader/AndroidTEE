package com.example.helloworld.crypto.ecc

import com.example.helloworld.crypto.aes.AESHelper
import com.example.helloworld.utils.HexUtil
import org.junit.Assert
import org.junit.Test
import java.security.MessageDigest
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey

class ECDHUnitTest {
    @Test
    fun sha256_isCorrect() {
        val data =
            "04be8ac2b0cc27d92b102b7fa25fc2d5aeb9ea5c4dfb88c74d4f8532c1ece317c8a47c6f7232f676c6c1ec46b8ab2a6687c7575b9892ae815a5f84248a946564f2".toByteArray()
        val hashHex = "b088cf414cbab06fff85602bbc27a3e24c96a757ee29c78c48b9eaa198686a12"
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(data)
        Assert.assertEquals(hashHex, HexUtil.bytesToHex(hash))
        Assert.assertEquals(hashHex, HexUtil.uBytesToHex(hash.toUByteArray()))

    }

    @Test
    fun generateSharedSecret_isCorrect() {
        val keypair = ECCP256.generateKeyPair()
        println("pubHex:${ECCP256.toPublicHex(keypair.public as ECPublicKey)}")

        val serverPubHex =
            "04deb43a5bb4c34cf8db53311d4d9f95d2356b8c011349ecb04fc00b73c303bc9dc0675f4ca45a562f589b993a94129482eb9b03f259ce8982e525927c3f70fdbe"
        val ecPubKey = ECCP256.fromPublicHex(serverPubHex)
        var aesKey = ECDH.generateSharedSecret(keypair.private as ECPrivateKey, ecPubKey)
        val aesHex = HexUtil.bytesToHex(aesKey)
        println("aesHex:$aesHex")
    }

}