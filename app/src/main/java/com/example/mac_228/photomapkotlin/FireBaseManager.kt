package com.example.mac_228.photomapkotlin

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.util.Base64
import android.util.Log
import com.example.mac_228.photomapkotlin.Activity.MainActivity
import com.example.mac_228.photomapkotlin.Models.FireBasePhotoModel
import com.example.mac_228.photomapkotlin.Models.PhotoModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.*
import kotlin.collections.ArrayList


object FireBaseManager : ValueEventListener {

    val TAG = "FireBaseManager"
    val mFireBaseAuth = FirebaseAuth.getInstance()
    lateinit var context: Context
    var mFirebaseInstance = FirebaseDatabase.getInstance()
    var mFirebaseDatabase: DatabaseReference? = null
    var myTask: MyTask? = null
    var newDataList: MutableList<PhotoModel>
    var  dataStatusForMap: Int = 0
    var  dataStatusForTimeLine: Int = 0

    init {
        newDataList = ArrayList()
    }

    fun updateData(id: Int, type: String, text: String) {
        mFirebaseInstance = FirebaseDatabase.getInstance()

        mFirebaseDatabase = mFirebaseInstance.getReference("photos/" + mFireBaseAuth.currentUser?.uid)

        mFirebaseDatabase?.child(id.toString())?.child("type")?.setValue(type)
        mFirebaseDatabase?.child(id.toString())?.child("text")?.setValue(text)
    }

    fun sendData(model: PhotoModel) {

        val lastQuery = mFirebaseDatabase?.orderByKey()

        lastQuery?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val id: Int?

                val person = dataSnapshot.value as? List<FireBasePhotoModel>

                if (person == null) {

                    val photo = FireBasePhotoModel(
                            model.id,
                            model.time,
                            model.coordinats,
                            model.text,
                            encodeImage(model.uri),
                            model.type
                    )
                    mFirebaseDatabase?.child((model.id).toString())?.setValue(photo)
                } else {
                    val map = person[person.size - 1] as Map<String, Long>
                    id = map["id"]?.toInt()

                    if(id == null){
                        return
                    }

                    val photo = FireBasePhotoModel(
                            id + 1,
                            model.time,
                            model.coordinats,
                            model.text,
                            encodeImage(model.uri),
                            model.type
                    )
                    mFirebaseDatabase?.child((id + 1).toString())?.setValue(photo)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.toString())
            }
        })

    }


    private fun encodeImage(imageUri: Uri): String {

        var imageStream: InputStream? = null
        try {
            imageStream = context.contentResolver.openInputStream(imageUri)
        } catch (e: FileNotFoundException) {
            Log.e(TAG, e.printStackTrace().toString())
        }
        val selectedImage = BitmapFactory.decodeStream(imageStream)
        val baos = ByteArrayOutputStream()
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val b = baos.toByteArray()
        val encImage = Base64.encodeToString(b, Base64.DEFAULT)

        return encImage
    }

    fun startDownloadData() {
        Log.d(TAG, "startdata")
        mFirebaseDatabase = mFirebaseInstance.getReference("photos/" + mFireBaseAuth.currentUser?.uid)
        Log.d(TAG, mFirebaseDatabase.toString())
        mFirebaseDatabase?.addValueEventListener(this)
    }
    fun stopDownloadData() {
        myTask?.cancel(true)
        myTask = null
        mFirebaseDatabase?.removeEventListener(this)
        mFirebaseDatabase = null
        newDataList.clear()
    }

    override fun onCancelled(p0: DatabaseError?) {
        Log.d(TAG, p0.toString())
    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        val list = ArrayList<FireBasePhotoModel>()
        Log.d(TAG, "dataCome")
        for (postSnapshot in dataSnapshot.children) {

            val model = postSnapshot.getValue(FireBasePhotoModel::class.java)
            list.add(model)
            Log.d(TAG, model.toString())
        }
        if (!list.isEmpty()) {
            myTask = MyTask(list)
            myTask?.execute()
        }
    }

    class MyTask(private val list: List<FireBasePhotoModel>) : AsyncTask<Void, Void, MutableList<PhotoModel>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d(TAG, "Start execute")
        }

        override fun doInBackground(vararg voids: Void): MutableList<PhotoModel>? {
            val newData = ArrayList<PhotoModel>()
            val root = Environment.getExternalStorageDirectory().toString() + "/PhotoMap_Folder/download_images/" + mFireBaseAuth.currentUser?.email + "/"
            var imageFolderPath: String
            val imagesFolder = File(root)
            if (!imagesFolder.exists()) {
                imagesFolder.mkdirs()
            }
            for (model in list) {
                if (isCancelled) {
                    Log.d(TAG, "isCancelld " + isCancelled)
                    return null
                }
                imageFolderPath = root + model.id + ".png"
                var fos: FileOutputStream? = null
                try {
                    if (model.photo != null) {
                        fos = FileOutputStream(File(imageFolderPath), true)
                        val decodedString = android.util.Base64.decode(model.photo, android.util.Base64.DEFAULT)
                        fos.write(decodedString)
                        fos.flush()
                        fos.close()
                    }

                } catch (e: Exception) {
                    Log.d(TAG, e.toString())
                } finally {
                    if (fos != null) {
                        fos = null
                    }
                }

                newData.add(PhotoModel(
                        model.id as Int,
                        Uri.fromFile(File(imageFolderPath)),
                        model.date as String,
                        model.type as String,
                        model.text as String,
                        model.latLang as String
                ))

                Log.d(TAG, PhotoModel(
                        model.id,
                        Uri.fromFile(File(imageFolderPath)),
                        model.date,
                        model.type,
                        model.text,
                        model.latLang
                ).toString())

            }

            return newData
        }

        override fun onPostExecute(photoSendModels: MutableList<PhotoModel>?) {
            super.onPostExecute(photoSendModels)

            if (photoSendModels != null) {
                Log.d(TAG, photoSendModels.toString())
                newDataList = photoSendModels

                dataStatusForMap = 1
                dataStatusForTimeLine = 1

                val intent = Intent(MainActivity.BROADCAST_ACTION)

                intent.putExtra(MainActivity.PARAM_TASK, MainActivity.STATUS_COME)

                context.sendBroadcast(intent)
            }
        }
    }

}