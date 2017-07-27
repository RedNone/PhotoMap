package com.example.mac_228.photomapkotlin.Activity

import android.animation.Animator
import android.animation.TimeInterpolator
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView

import com.example.mac_228.photomapkotlin.R
import com.github.chrisbanes.photoview.PhotoView

class FullPhotoActivity : AppCompatActivity() {

    companion object {
        val TAG = "FullPhotoActivity"
        val DATE_EXTRA = "dateExtra"
        val DISCRIPTION_EXTRA = "discriptionExtra"
        val IMAGE_URI = "imageURI"
    }

    lateinit var toolbar: Toolbar
    lateinit var image: PhotoView
    lateinit var layout: LinearLayout
    lateinit var time: TextView
    lateinit var description: TextView

    var isPhotoDescriptionsVisible = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_photo)

        toolbar = findViewById(R.id.toolbarFullPhoto) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.title = null
        toolbar.navigationIcon = resources.getDrawable(R.mipmap.ic_arrow_left)
        toolbar.setNavigationOnClickListener { finish() }

        image = findViewById(R.id.imageViewPhoto) as PhotoView
        layout = findViewById(R.id.layoutDescription) as LinearLayout
        time = findViewById(R.id.textViewPhotoDate) as TextView
        description = findViewById(R.id.textViewPhotoDescription) as TextView

        image.setOnClickListener { hideDescriptions() }

        getData()

    }

    private fun getData() {
        time.text = intent.getStringExtra(DATE_EXTRA)
        description.text = intent.getStringExtra(DISCRIPTION_EXTRA)
        image.setImageURI(Uri.parse(intent.getStringExtra(IMAGE_URI)))
    }

    private fun hideDescriptions() {
        when(isPhotoDescriptionsVisible){
            true -> {
                isPhotoDescriptionsVisible = false

                toolbar.animate().translationY((-toolbar.bottom).toFloat()).setInterpolator(AccelerateInterpolator()).setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {}
                    override fun onAnimationCancel(animator: Animator) {}
                    override fun onAnimationRepeat(animator: Animator) {}
                    override fun onAnimationEnd(animator: Animator) {
                        toolbar.visibility = View.INVISIBLE
                    }
                }).start()

                layout.animate().translationY(layout.bottom.toFloat()).setInterpolator(AccelerateInterpolator()).setListener(object : Animator.AnimatorListener {
                    override fun onAnimationEnd(animator: Animator) {
                        layout.visibility = View.INVISIBLE
                    }
                    override fun onAnimationStart(animator: Animator)  {}
                    override fun onAnimationCancel(animator: Animator) {}
                    override fun onAnimationRepeat(animator: Animator) {}
                }).start()
            }
            false -> {
                isPhotoDescriptionsVisible = true

                toolbar.animate().translationY(0f).setInterpolator(DecelerateInterpolator()).setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {
                        toolbar.visibility = View.VISIBLE
                    }
                    override fun onAnimationEnd(animator: Animator) {}
                    override fun onAnimationCancel(animator: Animator) {}
                    override fun onAnimationRepeat(animator: Animator) {}
                }).start()

                layout.animate().translationY(0f).setInterpolator(DecelerateInterpolator()).setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {
                        layout.visibility = View.VISIBLE
                    }
                    override fun onAnimationEnd(animator: Animator) {}
                    override fun onAnimationCancel(animator: Animator) {}
                    override fun onAnimationRepeat(animator: Animator) {}
                }).start()
            }
        }
    }
}
