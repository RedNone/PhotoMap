package com.example.mac_228.photomapkotlin


import android.app.ProgressDialog
import android.support.v4.app.Fragment


open class BaseFragment : Fragment(){

    protected var mProgressDialog: ProgressDialog? = null

    fun showProgressDialog() {
        if(mProgressDialog == null){
            mProgressDialog = ProgressDialog(context) .let{ j ->
                j.setMessage(context.getString(R.string.loading))
                j.isIndeterminate = true
                j
            }
        }

        mProgressDialog!!.show()
    }

    fun dismissProgressDialog() {
        if(mProgressDialog != null && mProgressDialog!!.isShowing){
            mProgressDialog!!.dismiss()
        }
    }
}
