package com.example.mac_228.photomapkotlin.Fragments


import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.mac_228.photomapkotlin.Activity.changeFragment
import com.example.mac_228.photomapkotlin.FireBaseManager
import com.example.mac_228.photomapkotlin.FragmentType
import com.example.mac_228.photomapkotlin.R


class LoginFragment : BaseFragment() {

    companion object {
        val TAG = "LoginFragment"
    }

    lateinit var mEmail: EditText
    lateinit var mPassword: EditText

    lateinit var loginButton: Button
    lateinit var createLoginButton: Button

    lateinit var mLayout: CoordinatorLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        mEmail = view.findViewById(R.id.emailEditText) as EditText
        mPassword = view.findViewById(R.id.passwordEditText) as EditText

        loginButton = view.findViewById(R.id.signInButton) as Button
        createLoginButton = view.findViewById(R.id.createLoginButton) as Button

        mLayout = view.findViewById(R.id.loginCoordinator) as CoordinatorLayout

        loginButton.setOnClickListener { signIn() }
        createLoginButton.setOnClickListener { createAcc() }

        setHasOptionsMenu(true)

        return view
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val action_logout = menu.findItem(R.id.action_logOut)
        val action_settings = menu.findItem(R.id.action_settings)
        val action_search = menu.findItem(R.id.action_search)

        action_logout.isVisible = false
        action_settings.isVisible = false
        action_search.isVisible = false
    }


    fun signIn() {
        val email = mEmail.text.toString()
        val password = mPassword.text.toString()

        if (!validateForm(email, password)) {
            return
        }

        showProgressDialog()

        FireBaseManager.mFireBaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        activity.changeFragment(FragmentType.MAIN)
                        Log.d(TAG, "signInWithEmail:success")
                    } else {
                        Log.e(TAG, "signInWithEmail:failure", result.exception)
                        Snackbar.make(mLayout, R.string.incorrectInput, Snackbar.LENGTH_SHORT).show()
                    }
                    this.dismissProgressDialog()
                }
    }

    fun createAcc() {
        val email = mEmail.text.toString()
        val password = mPassword.text.toString()

        if (!validateForm(email, password)) {
            return
        }

        showProgressDialog()

        FireBaseManager.mFireBaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        activity.changeFragment(FragmentType.MAIN)
                        Log.d(TAG, "createUserWithEmail:success")
                    } else {
                        Log.d(TAG, "createUserWithEmail:failure", result.exception)
                        Snackbar.make(mLayout, R.string.incorrectInput, Snackbar.LENGTH_SHORT).show()
                    }
                    this.dismissProgressDialog()
                }
    }


    fun validateForm(email: String, password: String): Boolean {
        var validateResult = true

        if (TextUtils.isEmpty(email)) {
            mEmail.error = context.getString(R.string.validError)
            validateResult = false
        }

        if (TextUtils.isEmpty(password)) {
            mPassword.error = context.getString(R.string.validError)
            validateResult = false
        }

        return validateResult
    }
}