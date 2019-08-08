package com.example.helloworld.crypto.aes

import org.junit.Assert
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun aes_helper_isCorrect() {
        var key = "1234567812345678"
        val key1=AESHelper.pkcs5Padding(key)
        Assert.assertEquals(key1.size, AESHelper.keyLength)

        key = "12345678"
        val key2=AESHelper.pkcs5Padding(key)
        Assert.assertEquals(key2.size, AESHelper.keyLength)

        key = "123456781234567812345678"
        val key3=AESHelper.pkcs5Padding(key)
        Assert.assertEquals(key3.size, AESHelper.keyLength)
        println("key1:$key1")
        println("key2:$key2")
        println("key3:$key3")
    }
}