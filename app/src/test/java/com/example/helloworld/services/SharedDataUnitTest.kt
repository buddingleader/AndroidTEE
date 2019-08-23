package com.example.helloworld.services

import com.example.helloworld.DisplayMessageActivity
import com.example.helloworld.crypto.hash.HashHelper
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

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

    @Test
    fun authorizeData_isCorrect() {
        val notificationID = "18729331d43554d62dc31e82cc129662afb67aaaa0ea2fca34ae9ec4f3008b2b"
        val status = 1
        val message = "1234"
        val encryptedType = 1
        val result = authorizeData(notificationID, status, message, encryptedType)
        println("result:$result")
    }

    @Test
    fun tee_isCorrect() {
/*
         // average is 30
         val data1 = getTestData(28)
         val data2 = getTestData(32)

         // upload
         val data1ID = upload(data1, "i'm 28 years old")
         val data2ID = upload(data2, "i'm 32 years old")
         println("data1ID: $data1ID")
         println("data2ID: $data2ID")

         // create task
         val dataIDs = listOf<String>(
             data1ID.toString(),
             data2ID.toString()
         )
         val algorithmID = "423956df9ab29aa3f7bb809e095eed64d98b8bea4c70ce9318f3b98ddfff90d1"
         val resultAddress = "/home/rabbit/teetest"
         val taskID = createExecutionTask(dataIDs, algorithmID, resultAddress)
         println("taskID: $taskID")

         // get task
         var task = queryTaskByID(taskID.toString())
         println("task: $task")
         var notifications = Common.parseNotifications(task)

        // upload data
        val fileID1 = uploadData(data1)
        val fileID2 = uploadData(data2)
        println("fileID1: $fileID1")
        println("fileID2: $fileID2")

        // authorize data
         val notification1ID = notifications?.get(data1ID).toString()
         val notification2ID = notifications?.get(data2ID).toString()
         val status = 1
         val encryptedType = 0
         val result1 = authorizeData(notification1ID, status, fileID1.toString(), encryptedType)
         val result2 = authorizeData(notification2ID, status, fileID2.toString(), encryptedType)
         println("result1:$result1")
         println("result2:$result2")
*/

        // execute task
        val taskID: String? = "dca991e12b07968c0522c35f143b2a9bf7b91203a5a7c96eee7a1a6d8e46755d"
        val result = executeTask(taskID.toString(), 1)
        println("result:$result")
    }

    fun getTestData(age: Int): ByteArray {
        var content = "姓名：路人甲\n" +
                "学历：本科\n" +
                "年龄：$age\n" +
                "联系方式：135xxxx1234"

        return content.toByteArray()
    }

    @get:Rule
    var folder = TemporaryFolder()

    fun uploadData(data:ByteArray):String?{
        val filename = HashHelper.sha256(data)
        val createdFile = folder.newFile(filename)
        createdFile.writeBytes(data)
        val fileID = DisplayMessageActivity.uploadFile(createdFile.absolutePath)

        return fileID
    }
}
