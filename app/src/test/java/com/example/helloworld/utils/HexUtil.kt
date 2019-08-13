package com.example.helloworld.utils

import org.junit.Assert
import org.junit.Test
import java.util.*

class HexUtilUnitTest {
    @Test
    fun bytesToHex_isCorrect() {
        val data =
            "04deb43a5bb4c34cf8db53311d4d9f95d2356b8c011349ecb04fc00b73c303bc9dc0675f4ca45a562f589b993a94129482eb9b03f259ce8982e525927c3f70fdbe"
        val bytes = HexUtil.hexToUBytes(data)
//        println("bytes:${bytes.contentToString()}")
        val hex = HexUtil.uBytesToHex(bytes)
//        println("hex:$hex")
        Assert.assertEquals(data, hex)
    }
}