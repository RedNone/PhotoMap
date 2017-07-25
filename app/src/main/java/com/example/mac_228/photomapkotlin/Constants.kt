package com.example.mac_228.photomapkotlin

/**
 * Created by Gilas on 7/25/17.
 */

enum class FragmentType {
    LOGIN,
    MAIN,
    ERROR
}

enum class RequestPermissions(val permission: Int) {
    StorageCameraLocation(1)
}