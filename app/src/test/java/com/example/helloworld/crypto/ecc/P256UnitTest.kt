package com.example.helloworld.crypto.ecc

import com.example.helloworld.utils.HexUtil
import org.bouncycastle.util.encoders.Hex
import org.junit.Assert
import org.junit.Test
import java.security.interfaces.ECPublicKey
import java.security.spec.InvalidKeySpecException
import java.util.*


class P256UnitTest {
    @Test
    fun generateKeyPair_isCorrect() {
        val keypair = ECCP256.generateKeyPair()
        println(HexUtil.bytesToHex(keypair.first))
        println(HexUtil.bytesToHex(keypair.second))
        var privKey = ECCP256.fromPrivateBytes(keypair.first)
        val pubKey = ECCP256.fromPublicBytes(keypair.second)

        val data = "Hello World!".toByteArray()
        var signature = ECCP256.sign(privKey, data)
        var verified = ECCP256.verify(pubKey, data, signature)
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
        var (_, pubBytes) = ECCP256.generateKeyPair()
        var ecPubHex = HexUtil.bytesToHex(pubBytes)
        var ecPubKey = ECCP256.fromPublicHex(ecPubHex)
        var clientPubKey = ECCP256.toPublicHex(ecPubKey)
        Assert.assertEquals(clientPubKey, ecPubKey)

        // Repeat check ten times
        for (count in 1..10) {
            var (_, pubBytes) = ECCP256.generateKeyPair()
            ecPubHex = HexUtil.bytesToHex(pubBytes)
            ecPubKey = ECCP256.fromPublicHex(ecPubHex)
            clientPubKey = ECCP256.toPublicHex(ecPubKey)
            Assert.assertEquals(clientPubKey, ecPubKey)
        }
    }

    @Test
    fun parsePrivateKey() {
        var serverPrivHex =
            "308187020100301306072a8648ce3d020106082a8648ce3d030107046d306b020101042062793dde394bba8051e33423077e46958afd913929f670913ffd9682317b44bca14403420004c0f4afb46d93fac0f71816697eae2b4dd3b264999d65000807ba2ef4a0e66973fafa60a2931d248e8e802918b4d939079a4afd89380c83d3a3cd264f21abef5e"
        var privBytes = HexUtil.hexToBytes(serverPrivHex)
        var privKey = ECCP256.fromPrivateBytes(privBytes)
        val pubKey =
            ECCP256.fromPublicHex("04c0f4afb46d93fac0f71816697eae2b4dd3b264999d65000807ba2ef4a0e66973fafa60a2931d248e8e802918b4d939079a4afd89380c83d3a3cd264f21abef5e")
        val data = "Hello World!".toByteArray()
        var signature = ECCP256.sign(privKey, data)
        var verified = ECCP256.verify(pubKey, data, signature)
        Assert.assertTrue(verified)

        // Invalid private hex
        serverPrivHex =
            "408187020100301306072a8648ce3d020106082a8648ce3d030107046d306b020101042062793dde394bba8051e33423077e46958afd913929f670913ffd9682317b44bca14403420004c0f4afb46d93fac0f71816697eae2b4dd3b264999d65000807ba2ef4a0e66973fafa60a2931d248e8e802918b4d939079a4afd89380c83d3a3cd264f21abef5e"
        try {
            privBytes = HexUtil.hexToBytes(serverPrivHex)
            ECCP256.fromPrivateBytes(privBytes)
        } catch (e: Exception) {
            Assert.assertTrue(e is InvalidKeySpecException)
        }

        // Another private hex
        serverPrivHex =
            "308187020100301306072a8648ce3d020106082a8648ce3d030107046d306b02010104204afef667d7cbc781934ac0f22b1d57a119630f6a59afd2dce3402a59c5f445dba14403420004e65b8c3e30f234d1c3b2397413a4c28e466fb8c0a820b2c2c75b0977cbce445d2dbd571341ff51e41045095ca31b05d46125627b14b153f8c84733cdf44398d0"
        privBytes = HexUtil.hexToBytes(serverPrivHex)
        privKey = ECCP256.fromPrivateBytes(privBytes)
        signature = ECCP256.sign(privKey, data)
        verified = ECCP256.verify(pubKey, data, signature)
        Assert.assertFalse(verified)
    }

    @Test
    fun formatPrivateKey() {
        var serverPrivHex =
            "308187020100301306072a8648ce3d020106082a8648ce3d030107046d306b020101042062793dde394bba8051e33423077e46958afd913929f670913ffd9682317b44bca14403420004c0f4afb46d93fac0f71816697eae2b4dd3b264999d65000807ba2ef4a0e66973fafa60a2931d248e8e802918b4d939079a4afd89380c83d3a3cd264f21abef5e"
        var privBytes = HexUtil.hexToBytes(serverPrivHex)
        var privKey = ECCP256.fromPrivateBytes(privBytes)
        var privHex = ECCP256.toPrivateHex(privKey)
        Assert.assertEquals(serverPrivHex, privHex)
    }

    @Test
    fun convertPemFile() {
        var serverPrivHex =
            "308187020100301306072a8648ce3d020106082a8648ce3d030107046d306b020101042062793dde394bba8051e33423077e46958afd913929f670913ffd9682317b44bca14403420004c0f4afb46d93fac0f71816697eae2b4dd3b264999d65000807ba2ef4a0e66973fafa60a2931d248e8e802918b4d939079a4afd89380c83d3a3cd264f21abef5e"
        var privBytes = HexUtil.hexToBytes(serverPrivHex)
        val encoded = ECC.toPEMFileContents(privBytes)
        Assert.assertNotNull(encoded)
        var data = ECC.fromPEMFileContents(String(encoded))
        Assert.assertEquals(privBytes.contentToString(), data.contentToString())
    }
}