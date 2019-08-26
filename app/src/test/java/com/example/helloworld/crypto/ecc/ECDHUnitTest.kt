package com.example.helloworld.crypto.ecc

import com.example.helloworld.crypto.aes.AESHelper
import com.example.helloworld.crypto.ecc.ECCP256.privateHexForTests
import com.example.helloworld.crypto.ecc.ECCP256.publicHexForTests
import com.example.helloworld.utils.HexUtil
import org.bouncycastle.util.encoders.Hex
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
        val serverPubHex =
            "048f03f8321b00a4466f4bf4be51c91898cd50d8cc64c6ecf53e73443e348d5925a16f88c8952b78ebac2dc277a2cc54c77b4c3c07830f49629b689edf63086293"
        val ecPubKey = ECCP256.fromPublicHex(serverPubHex)
        val priv = ECCP256.fromPrivateHex("308187020100301306072a8648ce3d020106082a8648ce3d030107046d306b02010104202d130ea6dac76fcae718fbd20bf146643aa66fe6e5902975d2c5ed6ab3bcb5e2a144034200048f03f8321b00a4466f4bf4be51c91898cd50d8cc64c6ecf53e73443e348d5925a16f88c8952b78ebac2dc277a2cc54c77b4c3c07830f49629b689edf63086293")
        val pub = ECCP256.fromPublicHex(publicHexForTests)
        var aesKey = ECDH.generateSharedSecret(priv, pub)

//        println("x:${ecPubKey.w.affineX}")
//        println("y:${ecPubKey.w.affineY}")
        println("x:${pub.w.affineX}")
        println("y:${pub.w.affineY}")
        println("s:${priv.s}")
        println("aesKey:${aesKey.toUByteArray().contentToString()}")
        val aesHex = HexUtil.bytesToHex(aesKey)
        println("aesHex:$aesHex")
    }

}