package com.example.helloworld

import org.junit.Assert
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.Rule


class FilesUnitTest {
    @get:Rule
    var folder = TemporaryFolder()

    @Test
    fun uploadFile_isCorrect() {
        val filename = "hello.txt"
        val createdFile = folder.newFile(filename)
        val data = "world!"
        createdFile.writeText(data, Charsets.UTF_8)

        val fileID = DisplayMessageActivity.uploadFile(createdFile.absolutePath)
        Assert.assertNotNull(fileID)

        val result = DisplayMessageActivity.downloadFile(fileID.toString())
        Assert.assertEquals(data, result)
    }
}
