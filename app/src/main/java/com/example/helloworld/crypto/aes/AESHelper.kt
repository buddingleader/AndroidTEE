package com.example.helloworld.crypto.aes

import com.example.helloworld.utils.HexUtil
import java.nio.ByteBuffer
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object AESHelper {
    private const val transformation = "AES/CBC/PKCS5Padding"
    const val keyLength = 32
    private const val ivLength = 16

    fun encrypt(plaintext: ByteArray, aesKey: ByteArray): String {
        val aesKey = pkcs5Padding(aesKey)

        val cipher = Cipher.getInstance(transformation)
        val keySpec = SecretKeySpec(aesKey, "AES")
        val ivSpec = IvParameterSpec(aesKey.copyOf(ivLength))
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

        val cipherTextBytes = cipher.doFinal(plaintext)
        return HexUtil.bytesToHex(cipherTextBytes)
    }

    fun decrypt(cipherText: String, aesKey: ByteArray): ByteArray {
        val aesKey = pkcs5Padding(aesKey)
        val cipher = Cipher.getInstance(transformation)
        val keySpec = SecretKeySpec(aesKey, "AES")
        val ivSpec = IvParameterSpec(aesKey.copyOf(ivLength))
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

        return cipher.doFinal(HexUtil.hexToBytes(cipherText))
    }

    fun pkcs5Padding(aesKey: ByteArray): ByteArray {
        if (aesKey.size < keyLength) {
            val paddingSize = keyLength - aesKey.size
            var padding = ByteArray(paddingSize)
            for (i in 0 until paddingSize) {
                padding[i] = 0
            }

            return padding.plus(aesKey)
        }

        return aesKey.copyOf(keyLength)
    }
}
