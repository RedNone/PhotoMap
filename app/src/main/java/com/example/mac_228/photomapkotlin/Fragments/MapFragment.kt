package com.example.mac_228.photomapkotlin.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mac_228.photomapkotlin.R

/**
 * A simple [Fragment] subclass.
 */
class MapFragment : BaseFragment() {

    companion object {
        val TAG = "MapFragment"
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        return view
    }

}