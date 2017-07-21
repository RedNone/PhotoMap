package com.example.mac_228.photomapkotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

class MainActivity : AppCompatActivity() {

    val FRAGMENT_LOGIN = 1
    val FRAGMENT_MAIN = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        if (FireBaseManager.mFireBaseAuth.currentUser == null) {
            changeFragment(FRAGMENT_LOGIN)
        } else {
            changeFragment(FRAGMENT_MAIN)
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
            FRAGMENT_MAIN -> Log.d("TAG", "MAINFRAGMENT")
        }

        fragmentTransaction.commit()
    }
}

