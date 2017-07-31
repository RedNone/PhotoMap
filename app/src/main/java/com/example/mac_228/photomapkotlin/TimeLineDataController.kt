package com.example.mac_228.photomapkotlin

import com.example.mac_228.photomapkotlin.Models.PhotoModel
import com.example.mac_228.photomapkotlin.Models.TimeLineDataModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TimeLineDataController(newDataList: MutableList<PhotoModel>) {

    var comeList: MutableList<PhotoModel> = ArrayList()
    var dataForTimeLine: MutableList<TimeLineDataModel> = ArrayList()

    init {
        for(obj in newDataList){
              comeList.add(obj.copy())
        }
        comeList.maxBy {
            getDate(it)
        }
        prepareNewList(comeList)
    }

    private fun getDate(model: PhotoModel): Long{
        val format = SimpleDateFormat("MMMM dd'th',yyyy - hh:mm a", Locale.ENGLISH)
        var date: Date? = null
        try {
            date = format.parse(model.time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if(date != null) {
            return date.time
        }
        else{
            return 0
        }
    }

    private fun prepareNewList(comeList: MutableList<PhotoModel>) {

        dataForTimeLine.clear()

        for(obj in comeList){
            dataForTimeLine.add(prepareTimeLineModel(obj))
        }

        for (i in dataForTimeLine.indices) {
            if (i == 0) {
                dataForTimeLine.add(i, TimeLineDataModel(dataForTimeLine[i].data?.mIndex as String))
                break
            }
            if (i + 1 != dataForTimeLine.size) {
                val firstIndex = dataForTimeLine[i].data?.mIndex ?: ""
                val nextIndex = dataForTimeLine[i + 1].data?.mIndex ?: ""
                if (!firstIndex.equals(nextIndex)) {
                    dataForTimeLine.add(i + 1, TimeLineDataModel(nextIndex))
                }
            }
        }
    }

    private fun  prepareTimeLineModel(model: PhotoModel): TimeLineDataModel {
        var newTime = ""

        val format = SimpleDateFormat("MMMM dd'th',yyyy - hh:mm a", Locale.ENGLISH)
        try {
            val date = format.parse(model.time)
            val newformat = SimpleDateFormat("yyyy'.'MM'.'dd", Locale.ENGLISH)
            newTime = newformat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }


        val newText = model.text.replace("#([A-Za-z0-9_-]+)".toRegex(), "")

        return TimeLineDataModel(model.id,
                model.uri,
                newTime,
                newText,
                model.type,
                getTimeString(model))
    }

    private fun getTimeString(model: PhotoModel): String? {

        var newTime: String? = null
        val format = SimpleDateFormat("MMMM dd'th',yyyy - hh:mm a", Locale.ENGLISH)
        try {
            val date = format.parse(model.time)
            val newformat = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
            newTime = newformat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return newTime
    }

    fun prepareHashTagsList(newText: String) {
        val mHashTagsList = ArrayList<PhotoModel>()

        comeList.filterTo(mHashTagsList) { it.text.contains(newText) }
        if (!mHashTagsList.isEmpty()) {
            prepareNewList(mHashTagsList)
        }
    }
}