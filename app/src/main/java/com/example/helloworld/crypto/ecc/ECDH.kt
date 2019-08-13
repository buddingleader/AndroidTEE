package com.example.helloworld.crypto.ecc

import com.example.helloworld.utils.HexUtil
import java.security.*
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey
import org.bouncycastle.jce.ECNamedCurveTable
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class ECDH {
    companion object {
        private val provider = org.bouncycastle.jce.provider.BouncyCastleProvider()
        private val iv: ByteArray = SecureRandom().generateSeed(16)

        // from https://gist.github.com/zcdziura/7652286
        fun generateKeyPair(): KeyPair {
            val parameterSpec = ECNamedCurveTable.getParameterSpec("brainpoolp256r1")
            val keyPairGenerator = KeyPairGenerator.getInstance("ECDH", provider)

            keyPairGenerator.initialize(parameterSpec)
            return keyPairGenerator.generateKeyPair()
        }

        fun generateSharedSecret(privateKey: ECPrivateKey, publicKey: ECPublicKey): ByteArray {
            val (x, _) = ECC.scalarMultiply(
                privateKey.params.curve, publicKey.w.affineX, publicKey.w.affineY, privateKey.s.toByteArray()
            )

            val digest = MessageDigest.getInstance("SHA-256")
            return digest.digest(x.toByteArray())
        }

        fun encrypt(key: SecretKey?, plainTextBytes: ByteArray): String {
            val ivSpec = IvParameterSpec(iv)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", provider)
            val cipherText: ByteArray

            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
            cipherText = ByteArray(cipher.getOutputSize(plainTextBytes.size))
            var encryptLength = cipher.update(
                plainTextBytes, 0,
                plainTextBytes.size, cipherText, 0
            )
            encryptLength += cipher.doFinal(cipherText, encryptLength)

            return HexUtil.bytesToHex(cipherText)
        }

        fun decrypt(key: SecretKey?, cipherText: String): ByteArray {
            val decryptionKey = SecretKeySpec(
                key?.encoded,
                key?.algorithm
            )
            val ivSpec = IvParameterSpec(iv)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding", provider)
            val cipherTextBytes = HexUtil.hexToBytes(cipherText)
            val plainText: ByteArray

            cipher.init(Cipher.DECRYPT_MODE, decryptionKey, ivSpec)
            plainText = ByteArray(cipher.getOutputSize(cipherTextBytes.size))
            var decryptLength = cipher.update(
                cipherTextBytes, 0,
                cipherTextBytes.size, plainText, 0
            )
            decryptLength += cipher.doFinal(plainText, decryptLength)

            return plainText
        }
    }
}