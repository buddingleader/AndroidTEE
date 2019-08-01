package com.example.helloworld.crypto.aes

import com.example.helloworld.utils.HexUtil
import java.nio.ByteBuffer
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


object AESHelper {
    var transformation = "AES/CBC/PKCS5Padding"
    var keyLength = 16

    fun encrypt(plainText: String, aesString: String): String {
        val aesKey = pkcs5Padding(aesString)
        val cipher = Cipher.getInstance(transformation)
        val keySpec = SecretKeySpec(aesKey, "AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)

        val cipherTextBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return HexUtil.bytesToHex(cipherTextBytes)
    }

    fun decrypt(cipherText: String, aesString: String): String {
        val aesKey = pkcs5Padding(aesString)
        val cipher = Cipher.getInstance(transformation)
        val keySpec = SecretKeySpec(aesKey, "AES")
        cipher.init(Cipher.DECRYPT_MODE, keySpec)

        val plainTextBytes = cipher.doFinal(HexUtil.hexToBytes(cipherText))
        return String(plainTextBytes, Charsets.UTF_8)
    }

    fun pkcs5Padding(aesString: String): ByteArray {
        val aesKey = aesString.toByteArray(Charsets.UTF_8)
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
