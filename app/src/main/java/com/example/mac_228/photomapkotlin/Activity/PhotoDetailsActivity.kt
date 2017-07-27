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

import com.example.mac_228.photomapkotlin.R

class PhotoDetailsActivity : AppCompatActivity() {

    companion object {
        val TAG = "PhotoDetailsActivity"
    }


    lateinit var image: ImageView
    lateinit var time: TextView
    lateinit var typeOfImage: TextView
    lateinit var description: EditText

    var date: String? = null
    var newImageUri: Uri? = null

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

    }

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
            R.id.action_accept -> Log.d(TAG,"")
        }

        return super.onOptionsItemSelected(item)
    }
}
