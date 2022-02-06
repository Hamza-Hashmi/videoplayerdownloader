package com.example.videoplayer_and_downloader.models

import android.os.Parcel
import android.os.Parcelable


data class MusicModel (

    var album:String? = null,
    var title: String? = null,
    var duration: String? = null,
    var path: String? = null,
    var artist: String? = null,
    var id:Int? =null,
    var playListId:Int? =null
    ) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(album)
        parcel.writeString(title)
        parcel.writeString(duration)
        parcel.writeString(path)
        parcel.writeString(artist)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MusicModel> {
        override fun createFromParcel(parcel: Parcel): MusicModel {
            return MusicModel(parcel)
        }

        override fun newArray(size: Int): Array<MusicModel?> {
            return arrayOfNulls(size)
        }
    }
}