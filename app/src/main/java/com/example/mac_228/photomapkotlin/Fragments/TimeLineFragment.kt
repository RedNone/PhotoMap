package com.example.mac_228.photomapkotlin.Fragments


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.widget.*
import android.view.*
import android.widget.ProgressBar
import com.example.mac_228.photomapkotlin.Activity.MainActivity
import com.example.mac_228.photomapkotlin.Adapters.TimeLineAdapter
import com.example.mac_228.photomapkotlin.FireBaseManager
import com.example.mac_228.photomapkotlin.R
import com.example.mac_228.photomapkotlin.TimeLineDataController


class TimeLineFragment : BaseFragment() {

    companion object {
        val TAG = "TimeLineFragment"
    }

    lateinit private var  searchMenuItem: MenuItem

    lateinit private var  mSearchView: SearchView
    lateinit private var  textListener: SearchView.OnQueryTextListener

    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var broadCastReceiver: BroadcastReceiver

    lateinit var dataController: TimeLineDataController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_time_line, container, false)

        recyclerView = view.findViewById(R.id.timeLineRecycler) as RecyclerView
        progressBar = view.findViewById(R.id.progressBarTimeLine) as ProgressBar

        broadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                    if(FireBaseManager.newDataList.isNotEmpty()){
                        dataController = TimeLineDataController(FireBaseManager.newDataList)
                        prepareRecyclerView()
                    }
            }
        }

        textListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if(FireBaseManager.newDataList.isNotEmpty()){
                    if (newText != null) {
                        dataController.prepareHashTagsList(newText)
                        prepareRecyclerView()
                    }
                }
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

        }

        setHasOptionsMenu(true)

        return  view
    }

    fun prepareRecyclerView(){
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        val layout = LinearLayoutManager(activity, OrientationHelper.VERTICAL, false)
        val adapter = TimeLineAdapter(dataController.dataForTimeLine, context)

        recyclerView.layoutManager = layout
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = DefaultItemAnimator()

        FireBaseManager.dataStatusForTimeLine = 0
    }



    override fun onResume() {
        super.onResume()

        val intFilter = IntentFilter(MainActivity.BROADCAST_ACTION)
        activity.registerReceiver(broadCastReceiver, intFilter)
        if(FireBaseManager.newDataList.isEmpty()){
            recyclerView.visibility = View.GONE
        }
        if(FireBaseManager.dataStatusForTimeLine == 1){
            dataController = TimeLineDataController(FireBaseManager.newDataList)
            prepareRecyclerView()
        }


    }

    override fun onPause() {
        super.onPause()
        activity.unregisterReceiver(broadCastReceiver)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {

        val action_logout = menu.findItem(R.id.action_logOut)
        val action_settings = menu.findItem(R.id.action_settings)
        val action_search = menu.findItem(R.id.action_search)

        action_settings.isVisible = true
        action_search.isVisible = true
        action_logout.isVisible = true
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        searchMenuItem = menu.findItem(R.id.action_search)
        mSearchView = searchMenuItem.actionView as SearchView
        mSearchView.setOnQueryTextListener(textListener)
    }

}