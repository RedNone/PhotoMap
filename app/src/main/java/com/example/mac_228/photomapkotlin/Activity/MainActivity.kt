package com.example.mac_228.photomapkotlin.Activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.example.mac_228.photomapkotlin.FireBaseManager
import com.example.mac_228.photomapkotlin.FragmentType
import com.example.mac_228.photomapkotlin.Fragments.ErrorFragment
import com.example.mac_228.photomapkotlin.Fragments.LoginFragment
import com.example.mac_228.photomapkotlin.Fragments.MainFragment
import com.example.mac_228.photomapkotlin.R
import com.example.mac_228.photomapkotlin.RequestPermissions
import com.google.firebase.FirebaseApp


class MainActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        FirebaseApp.initializeApp(this)

        if (FireBaseManager.mFireBaseAuth.currentUser == null) {
            changeFragment(FragmentType.LOGIN)
        } else {
            if (checkPermissions()) {
                changeFragment(FragmentType.MAIN)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logOut) {
            FireBaseManager.mFireBaseAuth.signOut()
            changeFragment(FragmentType.MAIN)
            return true
        }
        if (id == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RequestPermissions.StorageCameraLocation.ordinal -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    changeFragment(FragmentType.MAIN)
                } else {
                    changeFragment(FragmentType.ERROR)
                }
            }
        }
    }
}

fun FragmentActivity.changeFragment(fragmentType: FragmentType) {
    val fragmentTransaction = supportFragmentManager.beginTransaction()

    when (fragmentType) {
        FragmentType.LOGIN -> {
            if (supportFragmentManager.findFragmentByTag(LoginFragment.TAG) == null) {
                fragmentTransaction.replace(R.id.conteiner, LoginFragment(), LoginFragment.TAG)
            }
        }
        FragmentType.ERROR -> {
            if (supportFragmentManager.findFragmentByTag(ErrorFragment.TAG) == null) {
                fragmentTransaction.replace(R.id.conteiner, ErrorFragment(), ErrorFragment.TAG)
            }
        }
        FragmentType.MAIN -> {
            if (!checkPermissions()) {
                return
            }
            if (supportFragmentManager.findFragmentByTag(MainFragment.TAG) == null) {
                fragmentTransaction.replace(R.id.conteiner, MainFragment(), MainFragment.TAG)
            }
        }
    }

    fragmentTransaction.commit()
}


fun FragmentActivity.checkPermissions(): Boolean {
    val writePermission = (ContextCompat.checkSelfPermission
    (this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    val locationPermission = (ContextCompat.checkSelfPermission
    (this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    val cameraPermisson = (ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)

    if (!writePermission || !locationPermission || !cameraPermisson) {
        ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.CAMERA),
                RequestPermissions.StorageCameraLocation.ordinal)
    }

    return writePermission && locationPermission && cameraPermisson
}
