package com.example.helloworld.crypto.hash

import com.example.helloworld.utils.HexUtil
import java.security.MessageDigest

object HashHelper {

    fun sha256(str:String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val result = digest.digest(str.toByteArray())
        return HexUtil.bytesToHex(result)
    }
}