package com.example.helloworld.crypto.ecc

import android.content.*
import com.example.helloworld.utils.HexUtil
import java.security.*
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECParameterSpec
import java.security.spec.ECPublicKeySpec
import java.security.spec.PKCS8EncodedKeySpec


object ECCP256 {

    fun getParams(): ECParameterSpec {
        val keyPair = generateKeyPair()
        return fromPrivateBytes(keyPair.first).params
    }

    fun generateKeyPair(): Pair<ByteArray, ByteArray> {
        val kpg = KeyPairGenerator.getInstance("EC")
        kpg.initialize(256)
        val keyPair = kpg.generateKeyPair()
        val (privKey, pubKey) = Pair(keyPair.private as ECPrivateKey, keyPair.public as ECPublicKey)
        return Pair(privKey.encoded, ECC.marshal(privKey.params.curve, pubKey.w).toByteArray())
    }

    fun toPrivHex(privateKey: ECPrivateKey): String {
        return HexUtil.bytesToHex(privateKey.encoded)
    }

    fun fromPrivateBytes(privBytes: ByteArray): ECPrivateKey {
        val spec = PKCS8EncodedKeySpec(privBytes)
        val kf = KeyFactory.getInstance("EC")
        return kf.generatePrivate(spec) as ECPrivateKey
    }

    fun fromPublicBytes(pubBytes: ByteArray): ECPublicKey {
        ECC.unmarshal(getParams().curve, pubBytes.toUByteArray())
            .run {
                ECPublicKeySpec(this, getParams())
            }.run {
                KeyFactory.getInstance("EC").generatePublic(this)
            }.run {
                return this as ECPublicKey
            }
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