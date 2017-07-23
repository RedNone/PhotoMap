package com.example.mac_228.photomapkotlin.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.mac_228.photomapkotlin.Fragments.MapFragment
import com.example.mac_228.photomapkotlin.Fragments.TimeLineFragment

/**
 * Created by RedNone on 23.07.2017.
 */
class PagerAdapter(val fragmentManager: FragmentManager, val tabCount:Int) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment? {
        when(position){
            0 -> return MapFragment()
            1 -> return TimeLineFragment()
            else -> return null
        }
    }

    override fun getCount(): Int {
        return tabCount
    }
}