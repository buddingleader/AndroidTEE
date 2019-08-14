package com.example.helloworld.crypto.aes

import com.example.helloworld.utils.HexUtil
import org.junit.Assert
import org.junit.Test

class AESHelperUnitTest {
    @Test
    fun pkcs5Padding_isCorrect() {
        var key = "12345678123456781234567812345678".toByteArray()
        val key1 = AESHelper.pkcs5Padding(key)
        Assert.assertEquals(key1.size, AESHelper.keyLength)

        key = "1234567812345678".toByteArray()
        val key2 = AESHelper.pkcs5Padding(key)
        Assert.assertEquals(key2.size, AESHelper.keyLength)

        key = "123456781234567812345678123456781234567812345678".toByteArray()
        val key3 = AESHelper.pkcs5Padding(key)
        Assert.assertEquals(key3.size, AESHelper.keyLength)
    }

    @Test
    fun aes_isCorrect() {
        val data = "Hello World!".toByteArray()
        val secretKey = HexUtil.hexToBytes(
            "726cc22f046058c4e4173f11734c2705a83b3c9f73ad48a4b36ee476dbc6f4e2"
        )

        val ciphertext = AESHelper.encrypt(data, secretKey)
        Assert.assertEquals(ciphertext,"65b9269169d8896ad1a5428dc8a51465")
        val plaintext = AESHelper.decrypt(ciphertext, secretKey)
        Assert.assertEquals(data.contentToString(), plaintext.contentToString())
    }
}