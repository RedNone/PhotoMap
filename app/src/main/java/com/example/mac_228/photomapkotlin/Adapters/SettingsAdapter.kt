package com.example.mac_228.photomapkotlin.Adapters

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.mac_228.photomapkotlin.Activity.SettingsActivity
import com.example.mac_228.photomapkotlin.Models.SettingsModel
import com.example.mac_228.photomapkotlin.R
import org.w3c.dom.Text


class SettingsAdapter() : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    companion object {
        val APP_PREFERENCE = "PhotoMapSettings"
    }

    lateinit var list: List<SettingsModel>
    lateinit var context: Context
    lateinit var sPreference: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    constructor(list: List<SettingsModel>, context: Context) : this() {
        this.list = list
        this.context = context
        sPreference = context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE)
        editor = sPreference.edit()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemView = LayoutInflater.from(parent.context)
                                     .inflate(R.layout.settings_content, parent, false)

        return ViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val obj = list.get(position)

        holder.text.text = obj.text
        holder.text.setTextColor(ContextCompat.getColor(context, obj.color))

        when(obj.text){
            context.getString(R.string.default_type) ->
                holder.checkBox.buttonDrawable = ContextCompat.getDrawable(context, R.drawable.xml_checkbutton_defualt)
            context.getString(R.string.food) ->
                holder.checkBox.buttonDrawable = ContextCompat.getDrawable(context, R.drawable.xml_checkbutton_food)
            context.getString(R.string.friends) ->
                holder.checkBox.buttonDrawable = ContextCompat.getDrawable(context, R.drawable.xml_checkbutton_friends)
            context.getString(R.string.nature) ->
                holder.checkBox.buttonDrawable = ContextCompat.getDrawable(context, R.drawable.xml_checkbutton_nature)

        }

        if(sPreference.contains(obj.text)){
            holder.checkBox.isChecked = sPreference.getBoolean(obj.text,true)
        }

        holder.layout.setOnClickListener({
            if (sPreference.contains(obj.text)) {
                holder.checkBox.isChecked = !sPreference.getBoolean(obj.text, true)
            } else {
                holder.checkBox.isChecked = false
            }
        })


        holder.checkBox.setOnCheckedChangeListener({ _, b ->
            editor.putBoolean(obj.text, b)
            editor.apply()
            SettingsActivity.SETTINGS_UPDATE = true
        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView = itemView.findViewById(R.id.settingsTextView) as TextView
        var checkBox: CheckBox = itemView.findViewById(R.id.settingsCheckBox) as CheckBox
        var layout: LinearLayout = itemView.findViewById(R.id.settingLiner) as LinearLayout
    }
}