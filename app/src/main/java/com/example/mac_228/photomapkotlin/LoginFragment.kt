package com.example.mac_228.photomapkotlin


import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText


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

        return view
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