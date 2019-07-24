package com.example.helloworld.crypto.ecc

import com.example.helloworld.utils.HexUtil
import java.security.*
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey
import org.bouncycastle.jce.ECNamedCurveTable
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class ECDH {
    companion object {
        val provider = org.bouncycastle.jce.provider.BouncyCastleProvider()
        val iv: ByteArray = SecureRandom().generateSeed(16)


        fun generateKeyPair(): KeyPair {
            val parameterSpec = ECNamedCurveTable.getParameterSpec("brainpoolp256r1")
            val keyPairGenerator = KeyPairGenerator.getInstance("ECDH", provider)

            keyPairGenerator.initialize(parameterSpec)
            return keyPairGenerator.generateKeyPair()
        }

        fun generateSharedSecret(privateKey: PrivateKey, publicKey: PublicKey): SecretKey? {
            val keyAgreement = KeyAgreement.getInstance("ECDH", provider)
            keyAgreement.init(privateKey)
            keyAgreement.doPhase(publicKey, true)

            return keyAgreement.generateSecret("AES")
        }

        fun encrypt(key: SecretKey?, plainTextBytes: ByteArray): String {
            val ivSpec = IvParameterSpec(iv)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding", provider)
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