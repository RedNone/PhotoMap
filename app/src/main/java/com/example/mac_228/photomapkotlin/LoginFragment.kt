package com.example.mac_228.photomapkotlin


import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText


class LoginFragment : BaseFragment(), View.OnClickListener {

    companion object {
        val TAG = "LoginFragment"
    }

    lateinit var mEmail: EditText
    lateinit var mPassword: EditText

    lateinit var loginButton: Button
    lateinit var createLoginButton: Button

    lateinit var mLayout: CoordinatorLayout

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view: View = inflater!!.inflate(R.layout.fragment_login, container, false)

        mEmail = view.findViewById(R.id.emailEditText)
        mPassword = view.findViewById(R.id.passwordEditText)

        loginButton = view.findViewById(R.id.signInButton)
        createLoginButton = view.findViewById(R.id.createLoginButton)

        mLayout = view.findViewById(R.id.loginCoordinator)

        loginButton.setOnClickListener(this)
        createLoginButton.setOnClickListener(this)

        return view
    }


    fun signIn(){
        if(!validateForm( mEmail.text.toString(),mPassword.text.toString())) {
            return
        }
    }

    fun validateForm(email: String, password: String): Boolean{

        var validateResult: Boolean = true

        if(TextUtils.isEmpty(email)){
            mEmail.setError(context.getString(R.string.validError))
            validateResult = false
        }

        if(TextUtils.isEmpty(password)){
            mPassword.setError(context.getString(R.string.validError))
            validateResult = false
        }

        return validateResult
    }


    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.signInButton -> signIn()
            R.id.createLoginButton -> showProgressDialog()
        }
    }

}