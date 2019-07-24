package com.example.helloworld.crypto.aes

import com.example.helloworld.utils.HexUtil
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


object AESHelper {
    var transformation = "AES/CBC/PKCS5Padding"
    var keyLength = 16

    fun encrypt(plainText: String, aesString: String): String {
        val aesString = pkcs5Padding(aesString)
        val cipher = Cipher.getInstance(transformation)
        val keySpec = SecretKeySpec(aesString.toByteArray(Charsets.UTF_8), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)

        val cipherTextBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return HexUtil.bytesToHex(cipherTextBytes)
    }

    fun decrypt(cipherText: String, aesString: String): String {
        val aesString = pkcs5Padding(aesString)
        val cipher = Cipher.getInstance(transformation)
        val keySpec = SecretKeySpec(aesString.toByteArray(Charsets.UTF_8), "AES")
        cipher.init(Cipher.DECRYPT_MODE, keySpec)

        val plainTextBytes = cipher.doFinal(HexUtil.hexToBytes(cipherText))
        return String(plainTextBytes, Charsets.UTF_8)
    }

    fun pkcs5Padding(aesString:String):String{
        if (aesString.length<keyLength){
            val sb = StringBuffer(keyLength)
            for (i in 0 until keyLength-aesString.length) {
                sb.append("0")
            }
            sb.append(aesString)
            return sb.toString()
        }

        return aesString.substring(0,keyLength)
    }
}
