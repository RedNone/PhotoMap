package com.example.mac_228.photomapkotlin.Fragments


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.design.widget.TabLayout.OnTabSelectedListener
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mac_228.photomapkotlin.Adapters.PagerAdapter
import com.example.mac_228.photomapkotlin.R


/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment() {

    companion object {
        val TAG = "MainFragment"
    }

    lateinit var tabs: TabLayout
    lateinit var viewPager:ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        tabs = view.findViewById(R.id.tabLayout) as TabLayout
        viewPager = view.findViewById(R.id.pager) as ViewPager

        tabs.addTab(tabs.newTab().setText(R.string.map))
        tabs.addTab(tabs.newTab().setText(R.string.timeLine))

        tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab?.position ?: 0
            }

        })

        viewPager.adapter = PagerAdapter(childFragmentManager, tabs.tabCount)
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))

        return view
    }
}