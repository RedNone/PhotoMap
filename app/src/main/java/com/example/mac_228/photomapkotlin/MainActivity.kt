package com.example.mac_228.photomapkotlin

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.example.mac_228.photomapkotlin.Fragments.ErrorFragment
import com.example.mac_228.photomapkotlin.Fragments.LoginFragment
import com.example.mac_228.photomapkotlin.Fragments.MainFragment
import com.google.firebase.FirebaseApp


class MainActivity : AppCompatActivity() {

    companion object {
        val FRAGMENT_LOGIN = 1
        val FRAGMENT_MAIN = 2
        val FRAGMENT_ERROR = 3
    }

    val REQUEST_PERMISSON = 1
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        FirebaseApp.initializeApp(this)

        if (FireBaseManager.mFireBaseAuth.currentUser == null) {
            changeFragment(FRAGMENT_LOGIN)
        } else {
            if(checkPermissions()) {
                changeFragment(FRAGMENT_MAIN)
            }
        }
    }

   fun  checkPermissions(): Boolean {
        var writePermission = (ContextCompat.checkSelfPermission
                                (this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        var locationPermission = (ContextCompat.checkSelfPermission
                                    (this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        var cameraPermisson = (ContextCompat.checkSelfPermission(
                                        this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)

        if(!writePermission || !locationPermission || !cameraPermisson){
            ActivityCompat.requestPermissions(this,
                    arrayOf( android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                             android.Manifest.permission.ACCESS_COARSE_LOCATION,
                             android.Manifest.permission.CAMERA),
                             REQUEST_PERMISSON)
        }

        return writePermission && locationPermission && cameraPermisson
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_PERMISSON -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    changeFragment(FRAGMENT_MAIN)
                }
                else{
                    changeFragment(FRAGMENT_ERROR)
                }
            }
        }
    }

    fun changeFragment(typeOfFragment: Int) {

        val fragmentTransaction = supportFragmentManager.beginTransaction()

        when (typeOfFragment) {
            FRAGMENT_LOGIN -> {
                if (supportFragmentManager.findFragmentByTag(LoginFragment.TAG) == null) {
                    fragmentTransaction.replace(R.id.conteiner, LoginFragment(), LoginFragment.TAG)
                }
            }
            FRAGMENT_ERROR -> {
                if (supportFragmentManager.findFragmentByTag(ErrorFragment.TAG) == null) {
                    fragmentTransaction.replace(R.id.conteiner, ErrorFragment(), ErrorFragment.TAG)
                }
            }
            FRAGMENT_MAIN -> {
                if(!checkPermissions()){return}
                if (supportFragmentManager.findFragmentByTag(MainFragment.TAG) == null) {
                    fragmentTransaction.replace(R.id.conteiner, MainFragment(), MainFragment.TAG)
                }
            }
        }

        fragmentTransaction.commit()
    }
}

