package com.example.helloworld.utils

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils.toCharArray



object HexUtil {
    private val HEX_CHARS = "0123456789abcdef".toCharArray()

    fun bytesToHex(data: ByteArray): String {
        return data.toHex()
    }

    fun hexToBytes(encoded: String): ByteArray {
        if (encoded.length % 2 !== 0)
            throw IllegalArgumentException("Input string must contain an even number of characters")

        val result = ByteArray(encoded.length / 2)
        val enc = encoded.toCharArray()
        var i = 0
        while (i < enc.size) {
            val curr = StringBuilder(2)
            curr.append(enc[i]).append(enc[i + 1])
            result[i / 2] = Integer.parseInt(curr.toString(), 16).toByte()
            i += 2
        }
        return result
    }

    private fun ByteArray.toHex(): String {
        val result = StringBuffer()

        forEach {
            val octet = it.toInt()
            val firstIndex = (octet and 0xF0).ushr(4)
            val secondIndex = octet and 0x0F
            result.append(HEX_CHARS[firstIndex])
            result.append(HEX_CHARS[secondIndex])
        }

        return result.toString()
    }

    fun hexToUBytes(encoded: String): UByteArray {
        if (encoded.length % 2 !== 0)
            throw IllegalArgumentException("Input string must contain an even number of characters")

        val result = UByteArray(encoded.length / 2)
        val enc = encoded.toCharArray()
        var i = 0
        while (i < enc.size) {
            val curr = StringBuilder(2)
            curr.append(enc[i]).append(enc[i + 1])
            result[i / 2] = Integer.parseInt(curr.toString(), 16).toUByte()
            i += 2
        }
        return result
    }

    fun uBytesToHex(data: UByteArray): String {
        return data.toHex()
    }

    private fun UByteArray.toHex(): String {
        val result = StringBuffer()

        forEach {
            val octet = it.toInt()
            val firstIndex = (octet and 0xF0).ushr(4)
            val secondIndex = octet and 0x0F
            result.append(HEX_CHARS[firstIndex])
            result.append(HEX_CHARS[secondIndex])
        }

        return result.toString()
    }
}
