package com.example.mac_228.photomapkotlin.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mac_228.photomapkotlin.Activity.checkPermissions
import com.example.mac_228.photomapkotlin.R

class ErrorFragment : Fragment() {

    companion object {
        val TAG = "ErrorFragment"
    }

    lateinit var errorText: TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_error, container, false)

        errorText = view.findViewById(R.id.textViewError) as TextView
        errorText.setOnClickListener { activity.checkPermissions() }

        return view
    }
}
