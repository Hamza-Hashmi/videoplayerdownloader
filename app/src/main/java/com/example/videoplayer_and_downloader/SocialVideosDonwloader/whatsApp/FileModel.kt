package com.example.videodownload.whatsApp

import androidx.annotation.Keep
import java.io.File
import java.io.Serializable

@Keep
data class FileModel(var name: File = File(""), var type: Int? = null): Serializable {
    companion object {
        val  Video : Int = 1
    }

}
