package com.example.helloworld.crypto.ecc

import org.junit.Assert
import org.junit.Test

class ECDHUnitTest {
    @Test
    fun ecdh_isCorrect() {
        val keyPair = ECDH.generateKeyPair()
        val keyPair1 = ECDH.generateKeyPair()
        var secretKey = ECDH.generateSharedSecret(keyPair.private, keyPair1.public)
        var secretKey1 = ECDH.generateSharedSecret(keyPair1.private, keyPair.public)
//        println("secretKey:$secretKey")
//        println("secretKey1:$secretKey1")
        Assert.assertEquals(secretKey, secretKey1)

        // encrypt
        val data = "Hello World!"
        var cipherText = ECDH.encrypt(secretKey, data.toByteArray(Charsets.UTF_8))
        var cipherText1 = ECDH.encrypt(secretKey1, data.toByteArray(Charsets.UTF_8))
//        println("cipherText:cipherText")
//        println("cipherText1:cipherText1")
        Assert.assertEquals(cipherText, cipherText1)

        // decrypt
        var plainText = String(ECDH.decrypt(secretKey, cipherText1),Charsets.UTF_8)
        var plainText1 = String(ECDH.decrypt(secretKey1, cipherText),Charsets.UTF_8)
//        println("plainText:$plainText")
//        println("plainText1:$plainText1")
        Assert.assertEquals(plainText, plainText1)
        Assert.assertEquals(plainText, data)
    }
}