package com.example.mac_228.photomapkotlin.Models

import android.net.Uri

data class PhotoModel(val id: Int,
                      val uri: Uri,
                      var time: String,
                      val type: String,
                      val text: String,
                      val coordinats: String)