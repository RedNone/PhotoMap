package com.example.mac_228.photomapkotlin


import android.app.ProgressDialog
import android.support.v4.app.Fragment


open class BaseFragment : Fragment() {

    protected var mProgressDialog: ProgressDialog? = null

    fun showProgressDialog() {
        val progressDialog = mProgressDialog ?: createProgressDialog()
        mProgressDialog = progressDialog
        progressDialog.show()
    }

    private fun createProgressDialog(): ProgressDialog {
        val dialog = ProgressDialog(context)
        dialog.setMessage(context.getString(R.string.loading))
        dialog.isIndeterminate = true
        return dialog
    }

    fun dismissProgressDialog() {
        val progressDialog = mProgressDialog

        if (progressDialog != null && progressDialog.isShowing) {
            progressDialog.dismiss()
            mProgressDialog = null
        }
    }
}
