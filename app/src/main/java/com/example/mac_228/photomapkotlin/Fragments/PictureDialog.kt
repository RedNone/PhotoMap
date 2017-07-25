package com.example.mac_228.photomapkotlin.Fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.example.mac_228.photomapkotlin.R


class PictureDialog() : DialogFragment() {

    companion object {
        val TAG = "PictureDialog"
    }

    lateinit var mapObject: MapFragment

    constructor(mapObject: MapFragment) : this() {
        this.mapObject = mapObject
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.photo)
               .setItems(R.array.dialog_varibale) { _, i ->
                    when (i) {
                        0 -> {
                            val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            mapObject.startActivityForResult(pickPhoto, targetRequestCode)
                        }
                        1 -> {
                            val takePhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            mapObject.initializeImageUri()
                            if (mapObject.fileUri != null) {
                                takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, mapObject.fileUri)
                                mapObject.startActivityForResult(takePhoto, targetRequestCode)
                            }
                            else {
                                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
                            }

                        }

                    }

                }


        return builder.create()
    }
}