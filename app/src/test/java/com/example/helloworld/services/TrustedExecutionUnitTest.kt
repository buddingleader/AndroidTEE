package com.example.helloworld.services

import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TrustedExecutionUnitTest {
    @Test
    fun createTask_isCorrect() {
        val dataIDs = listOf<String>(
            "0f7e7da72cf3d1b5ba6807f05a8f4cba3cfe3426d88c310c8e4b2794a18b5b6e",
            "85001bf47a47238db1d15391690f55dc72e5de105fe73816587fe1f774020614",
            "e5b02dd30ebd692fcc59026bc843392991fc6af8b9286a43de32dba2473b2c2a"
        )
        val algorithmID = "c6bd57860951a505ad9d74ac4ed2f192196bfda58336d450eea4e3dffbc9c9ed"
        val resultAddress = "/home/rabbit/teetest"
        val taskID = createExecutionTask(dataIDs, algorithmID, resultAddress)
        println("taskID: $taskID")

        var result = queryTaskByID("23782514abc87315a7cc40a1384b1db1c930946e7e65ed2e42cbd803e92d3efc")
        println("result: $result")

    }

//    @Test
//    fun
}