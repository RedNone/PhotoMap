package com.example.mac_228.photomapkotlin.Models

import android.net.Uri


class TimeLineDataModel {

    var data: Data? = null
    var sectionName: String? = null

    constructor( mId: Int,
                 mUri: Uri,
                 mTime: String,
                 mText: String,
                 mType: String,
                 mIndex: String?){
        data = Data(mId, mUri, mTime, mText, mType, mIndex)
    }

    constructor(sectionName: String){
        this.sectionName = sectionName
    }

    data class Data(var mId: Int,
                    var mUri: Uri,
                    var mTime: String,
                    var mText: String,
                    val mType: String,
                    val mIndex: String?)
}