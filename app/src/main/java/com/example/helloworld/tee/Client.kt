package com.example.helloworld.tee

import com.example.helloworld.DisplayMessageActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.extensions.jsonBody
import java.io.File

object Client{
    val TEE_SERVER_ADDRESS = "http://192.168.127.129:8060/v1"

    // `curl -sX POST "http://localhost:8060/v1/tee/" -H  "accept: application/json"
    // -H  "content-type: application/x-www-form-urlencoded"
    // -d "ciphertext=A.secret&summary=$A_summary&description=$A_description&owner=$A_owner"`
    fun createSharedData(hash:String){
        val owner = ""
        Fuel.post("$TEE_SERVER_ADDRESS/tee/")
            .jsonBody("{ \"ciphertext\" : \"foo\" }, \"summary\" : \"$hash\" }, \"description\" : \"bar\" }, \"owner\" : \"$owner\" }")
//        Fuel.upload("${DisplayMessageActivity.SERVER_ADDRESS}/files")
//            .add { FileDataPart(File(pathname), name = "file", filename = pathname) }
//            .responseString().run {
//                val (result, error) = this.third
//                if (error != null) {
//                    println("upload file[$pathname] error: $error")
//                }
//
//                return result?.split("file_id: ")?.get(1)
//            }
    }
}