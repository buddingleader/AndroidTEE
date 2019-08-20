package com.example.helloworld.crypto.ecc

import com.example.helloworld.utils.HexUtil
import java.security.*
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECParameterSpec
import java.security.spec.ECPublicKeySpec
import java.security.spec.PKCS8EncodedKeySpec


object ECCP256 {
    const val privateHexForTests =
        "3041020100301306072a8648ce3d020106082a8648ce3d030107042730250201010420dcaf56fca9bcf92aac0a04867687d7260d1d6593647c960a4477302835c664f9"
    const val publicHexForTests =
        "0445036915380e64af21dad477f3b7460a5fd35cc369193581809fe9e8f62a20727b9f822d6395f05d8287876667989c504cf2323b59dfed074d09dee8be4beb04"

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

    fun fromPrivateHex(privateHex: String): ECPrivateKey {
        val privateBytes = HexUtil.hexToBytes(privateHex)
        return fromPrivateBytes(privateBytes)
    }

    fun toPrivateHex(privateKey: ECPrivateKey): String {
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