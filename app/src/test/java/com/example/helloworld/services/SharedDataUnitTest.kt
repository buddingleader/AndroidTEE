package com.example.helloworld.services

import com.example.helloworld.DisplayMessageActivity
import com.example.helloworld.crypto.aes.AESHelper
import com.example.helloworld.crypto.ecc.ECCP256
import com.example.helloworld.crypto.ecc.ECCP256.privateHexForTests
import com.example.helloworld.crypto.ecc.ECCP256.publicHexForTests
import com.example.helloworld.crypto.ecc.ECDH
import com.example.helloworld.crypto.hash.HashHelper
import com.example.helloworld.utils.HexUtil
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
        // average is 30
        val data1 = getTestData(22)
        val data2 = getTestData(32)

        // upload
        val data1ID = upload(data1, "i'm 28 years old")
        val data2ID = upload(data2, "i'm 32 years old")
        println("data1ID: $data1ID")
        println("data2ID: $data2ID")
        Thread.sleep(2_000)

        // create task
        val dataIDs = listOf<String>(
            data1ID.toString(),
            data2ID.toString()
        )
        val algorithmID = "423956df9ab29aa3f7bb809e095eed64d98b8bea4c70ce9318f3b98ddfff90d1"
        val resultAddress = "/home/rabbit/teetest"
        val taskID = createExecutionTask(dataIDs, algorithmID, resultAddress)
        println("taskID: $taskID")
        Thread.sleep(3_000)

        // get task
        var task = queryTaskByID(taskID.toString())
        println("task: $task")
        val notifications = Common.parseNotifications(task)
        val requester = Common.parseRequester(task)
        println("requester: $requester")


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
        Thread.sleep(2_000)

        // execute task - val taskID: String? = "4000b112dc92888e83b3cc9eaa414bb365599e7a689f7f66cc902c5cba6d6168"
        val result = executeTask(taskID.toString(), 1)
        println("result:$result")

        // download result
//        val result:String? = "662cb7ad704fd826e5ec20fd51f859e6506e9b99ccb15949d208a85b403ed5fe"
        val logID = HashHelper.sha256((result + publicHexForTests).toByteArray())
//        println("logID:$logID")
        val log = DisplayMessageActivity.downloadFile(logID)
        println("log:$log")

        // secret key
//        val requester =
//            "048f03f8321b00a4466f4bf4be51c91898cd50d8cc64c6ecf53e73443e348d5925a16f88c8952b78ebac2dc277a2cc54c77b4c3c07830f49629b689edf63086293"
        val ecPubKey = ECCP256.fromPublicHex(requester.toString())
        var aesKey = ECDH.generateSharedSecret(ECCP256.fromPrivateHex(privateHexForTests), ecPubKey)
        val aesHex = HexUtil.bytesToHex(aesKey)
        println("aesHex:$aesHex")

        // parse log result
        val logResult = AESHelper.decrypt(log.toString(), aesKey)
        println("logResult:${String(logResult)}")

    }

    private fun getTestData(age: Int): ByteArray {
        var content = "姓名：路人甲\n" +
                "学历：本科\n" +
                "年龄：$age\n" +
                "联系方式：135xxxx1234"

        return content.toByteArray()
    }

    @get:Rule
    var folder = TemporaryFolder()

    private fun uploadData(data: ByteArray): String? {
        val filename = HashHelper.sha256(data)
        val createdFile = folder.newFile(filename)
        createdFile.writeBytes(data)
        return DisplayMessageActivity.uploadFile(createdFile.absolutePath)
    }
}
