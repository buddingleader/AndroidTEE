package com.example.helloworld.utils

object HexUtil {

    private fun bytesToHex(data: ByteArray, length: Int): String {
        val digits = "0123456789ABCDEF"
        val buffer = StringBuffer()
        for (i in 0 until length) {
            val v = data[i].toInt() and 0xff
            buffer.append(digits[v shr 4])
            buffer.append(digits[v and 0xf])
        }

        return buffer.toString()
    }

    fun bytesToHex(data: ByteArray): String {
        return bytesToHex(data, data.size)
    }

    fun hexToBytes(string: String): ByteArray {
        val length = string.length
        val data = ByteArray(length / 2)
        var i = 0
        while (i < length) {
            data[i / 2] = ((Character.digit(string[i], 16) shl 4) + Character.digit(string[i + 1], 16)).toByte()
            i += 2
        }

        return data
    }
}
