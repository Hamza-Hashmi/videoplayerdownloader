package com.example.videodownload.list_helper

import androidx.annotation.IntDef

class ItemType {
    companion object {
        const val RealItem = 1

        @IntDef(RealItem)
        @Retention(AnnotationRetention.SOURCE)
        annotation class ItemType
    }
}