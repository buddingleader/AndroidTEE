package com.example.helloworld.services

import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SharedDataUnitTest {
    @Test
    fun upload_isCorrect() {
        val dataID = upload("Hello World!".toByteArray(), "Test")
        Assert.assertNotNull(dataID)
//        var result = queryDataByID("ae5073670daf3316850e8855a1e024559a51d75f51a0c7a9ec1dc3a861ce03b1")

//        println("result: $result")
    }

//    @Test
//    fun
}
