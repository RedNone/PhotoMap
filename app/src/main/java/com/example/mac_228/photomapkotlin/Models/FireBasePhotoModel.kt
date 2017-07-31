package com.example.mac_228.photomapkotlin.Models

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
class FireBasePhotoModel(val id: Int? = null,
                         val date: String? = null,
                         val latLang: String? = null,
                         val text: String? = null,
                         val photo: String? = null,
                         val type: String? = null)