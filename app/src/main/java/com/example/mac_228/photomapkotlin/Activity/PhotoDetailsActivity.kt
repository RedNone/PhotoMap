package com.example.mac_228.photomapkotlin.Activity

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.mac_228.photomapkotlin.FireBaseManager
import com.example.mac_228.photomapkotlin.ImageType
import com.example.mac_228.photomapkotlin.Models.PhotoModel

import com.example.mac_228.photomapkotlin.R
import java.text.SimpleDateFormat
import java.util.*

class PhotoDetailsActivity : AppCompatActivity() {

    companion object {
        val TAG = "PhotoDetailsActivity"
        val TYPE_OF_IMAGE = "typeImage"
        val NEW_IMAGE_URI = "newImageUri"
        val NEW_IMAGE_LOCATION = "newImageLocation"
        val EXISTING_PHOTO_ID = "existingPhotoId"
        val NEW_IMAGE_BUTTON = 1
        val NEW_IMAGE_LONGPRESS = 2
        val EXISTING_PHOTO = 3
    }


    lateinit var image: ImageView
    lateinit var time: TextView
    lateinit var typeOfImage: TextView
    lateinit var description: EditText

    lateinit var date: String
    lateinit var newImageUri: Uri
    lateinit var coordinats: String
    var isPhotoExisting = false

    private var  mIdOfExistingPhoto: Int = 0
    lateinit private var  mObjectOfExistingPhoto: PhotoModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_details)

        val toolbar = findViewById(R.id.photoToolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = null

        toolbar.navigationIcon = resources.getDrawable(R.mipmap.ic_arrow_left)
        toolbar.setNavigationOnClickListener { finish() }
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        image = findViewById(R.id.imageViewPhotoDetails) as ImageView
        time = findViewById(R.id.timeTextView) as TextView
        typeOfImage = findViewById(R.id.typeTextView) as TextView
        description = findViewById(R.id.descriptionEditText) as EditText


        image.setOnClickListener {
           startActivity(prepareIntent() ?: return@setOnClickListener)
        }

        registerForContextMenu(typeOfImage)

        checkData()
    }

    private fun checkData() {
        if (intent.getIntExtra(TAG, 0) == NEW_IMAGE_BUTTON) {

            typeOfImage.setText(R.string.friends)

            date = getFormatDate()

            time.text = date

            coordinats = intent.getStringExtra(NEW_IMAGE_LOCATION)

            if (intent.getSerializableExtra(TYPE_OF_IMAGE) == ImageType.GALLERY_TYPE) {
                newImageUri = Uri.parse(intent.getStringExtra(NEW_IMAGE_URI))
                image.setImageURI(newImageUri)
            }
            if (intent.getSerializableExtra(TYPE_OF_IMAGE) == ImageType.CAMERA_TYPE) {
                newImageUri = Uri.parse(intent.getStringExtra(NEW_IMAGE_URI))
                image.setImageURI(newImageUri)
            }
            isPhotoExisting = false
        }
        if (intent.getIntExtra(TAG, 0) == NEW_IMAGE_LONGPRESS) {
            typeOfImage.setText(R.string.friends)

            date = getFormatDate()

            time.text = date

            coordinats = intent.getStringExtra(NEW_IMAGE_LOCATION)

            if (intent.getSerializableExtra(TYPE_OF_IMAGE) == ImageType.GALLERY_TYPE) {
                newImageUri = Uri.parse(intent.getStringExtra(NEW_IMAGE_URI))
                image.setImageURI(newImageUri)
            }
            if (intent.getSerializableExtra(TYPE_OF_IMAGE) == ImageType.CAMERA_TYPE) {
                newImageUri = Uri.parse(intent.getStringExtra(NEW_IMAGE_URI))
                image.setImageURI(newImageUri)
            }
            isPhotoExisting = false
        }

        if (intent.getIntExtra(TAG, 0) == EXISTING_PHOTO) {
            mIdOfExistingPhoto = Integer.valueOf(intent.getStringExtra(EXISTING_PHOTO_ID))!!
            mObjectOfExistingPhoto = FireBaseManager.newDataList[mIdOfExistingPhoto]
            typeOfImage.text = mObjectOfExistingPhoto.type
            time.text = mObjectOfExistingPhoto.time
            date = mObjectOfExistingPhoto.time

            if (!TextUtils.isEmpty(mObjectOfExistingPhoto.text)) {
                description.setText(mObjectOfExistingPhoto.text)
            }
            newImageUri = mObjectOfExistingPhoto.uri
            image.setImageURI(newImageUri)

            isPhotoExisting = true
        }
    }

    private fun getFormatDate(): String = SimpleDateFormat("MMMM dd'th',yyyy - hh:mm a", Locale.ENGLISH).format(Date())


    private fun prepareIntent(): Intent? {

        if(newImageUri == null || date == null) {
            Snackbar.make(description, R.string.error, Snackbar.LENGTH_SHORT).show()
            return null
        }

        val intent = Intent(this, FullPhotoActivity::class.java)
        val str = description.text.toString()
        intent.putExtra(FullPhotoActivity.DATE_EXTRA, date)
        intent.putExtra(FullPhotoActivity.DISCRIPTION_EXTRA, str)
        intent.putExtra(FullPhotoActivity.IMAGE_URI, newImageUri.toString())

        return intent
    }

    private fun prepareNewPhotoData(): PhotoModel {
        val newdata = PhotoModel(
                0,
                newImageUri,
                date,
                typeOfImage.text.toString(),
                description.text.toString(),
                coordinats

        )
        return newdata
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.friends_menu -> typeOfImage.setText(R.string.friends)
            R.id.nature_menu -> typeOfImage.setText(R.string.nature)
            R.id.defualt_menu -> typeOfImage.setText(R.string.default_type)
            R.id.food_menu -> typeOfImage.setText(R.string.food)
        }

        return super.onContextItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_photo, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_accept -> {
                if(isPhotoExisting) {
                    FireBaseManager.updateData(mIdOfExistingPhoto, typeOfImage.text.toString(), description.text.toString())
                } else {
                    FireBaseManager.sendData(prepareNewPhotoData())
                    Log.d(TAG, prepareNewPhotoData().toString())
                }
            }
        }
        finish()
        return super.onOptionsItemSelected(item)
    }
}
