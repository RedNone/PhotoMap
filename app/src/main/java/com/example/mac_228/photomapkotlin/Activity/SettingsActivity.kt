package com.example.mac_228.photomapkotlin.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.example.mac_228.photomapkotlin.Adapters.SettingsAdapter
import com.example.mac_228.photomapkotlin.Models.SettingsModel

import com.example.mac_228.photomapkotlin.R

class SettingsActivity : AppCompatActivity() {

    companion object {
        var SETTINGS_UPDATE = false
    }

    lateinit var toolbar: Toolbar
    lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        toolbar = findViewById(R.id.toolbarSettings) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.title = null
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_left)
        toolbar.setNavigationOnClickListener { finish() }

        recycler = findViewById(R.id.settingsRecycler) as RecyclerView

        val linerManager = LinearLayoutManager(this)
        recycler.layoutManager = linerManager

        recycler.adapter = SettingsAdapter(initList(), this)

    }

    private fun  initList(): List<SettingsModel> {
        var list = ArrayList<SettingsModel>()
        list.add(SettingsModel(0, getString(R.string.nature), R.color.settings_nature))
        list.add(SettingsModel(1, getString(R.string.friends), R.color.settings_friends))
        list.add(SettingsModel(2, getString(R.string.default_type), R.color.settings_default))
        list.add(SettingsModel(3, getString(R.string.food), R.color.settings_food))
        return list
    }
}
