package com.example.helloworld.crypto.ecc

import com.example.helloworld.utils.HexUtil
import java.security.*
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECParameterSpec
import java.security.spec.ECPublicKeySpec


object ECCP256 {

    fun getParams(): ECParameterSpec {
        val keyPair = generateKeyPair()
        val pubKey = keyPair.public as ECPublicKey
        return pubKey.params
    }

fun generateKeyPair(): KeyPair {
    val kpg = KeyPairGenerator.getInstance("EC")
    kpg.initialize(256)
    return kpg.generateKeyPair()
}

fun fromPublicHex(pubHex: String): ECPublicKey {
    HexUtil.hexToUBytes(pubHex).run {
        ECC.unmarshal(getParams().curve, this)
    }.run {
        ECPublicKeySpec(this, getParams())
    }.run {
        KeyFactory.getInstance("EC").generatePublic(this)
    }.run {
        return this as ECPublicKey
    }
}

    fun toPublicHex(publicKey: ECPublicKey): String {
        val pubBytes = ECC.marshal(publicKey.params.curve, publicKey.w)
        return HexUtil.uBytesToHex(pubBytes)
    }

    fun sign(privateKey: PrivateKey, data: ByteArray): ByteArray? {
        return Signature.getInstance("SHA256withECDSA").run {
            initSign(privateKey)
            update(data)
            sign()
        }
    }

    fun verify(publicKey: PublicKey, data: ByteArray, signature: ByteArray?): Boolean {
        return Signature.getInstance("SHA256withECDSA").run {
            initVerify(publicKey)
            update(data)
            verify(signature)
        }
    }


}