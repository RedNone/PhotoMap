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

        comeList.forEach { dataForTimeLine.add(prepareTimeLineModel(it)) }

        for (i in dataForTimeLine.indices) {
            if (i == 0) {
                dataForTimeLine.add(i, TimeLineDataModel(dataForTimeLine[i].data?.mIndex as String))
                break
            }
            if (i + 1 != dataForTimeLine.size) {
                val firstIndex = dataForTimeLine[i].data?.mIndex ?: ""
                val nextIndex = dataForTimeLine[i + 1].data?.mIndex ?: ""
                if (firstIndex != nextIndex) {
                    dataForTimeLine.add(i + 1, TimeLineDataModel(nextIndex))
                }
            }
        }
    }

    private fun  prepareTimeLineModel(model: PhotoModel): TimeLineDataModel {
        val time = getTimeString(model.time)

        val newText = model.text.replace("#([A-Za-z0-9_-]+)".toRegex(), "")

        return TimeLineDataModel(model.id,
                model.uri,
                time.second,
                newText,
                model.type,
                time.first)
    }

    private fun getTimeString(time: String): Pair<String, String> {

        var newTime = ""
        var newTime2 = ""
        val format = SimpleDateFormat("MMMM dd'th',yyyy - hh:mm a", Locale.ENGLISH)
        try {
            val date = format.parse(time)
            val newformat = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
            val newformat2 = SimpleDateFormat("yyyy'.'MM'.'dd", Locale.ENGLISH)
            newTime = newformat.format(date)
            newTime2 = newformat2.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return Pair(newTime, newTime2)
    }

    fun prepareHashTagsList(newText: String) {
        val mHashTagsList = ArrayList<PhotoModel>()

        comeList.filterTo(mHashTagsList) { it.text.contains(newText) }
        if (!mHashTagsList.isEmpty()) {
            prepareNewList(mHashTagsList)
        }
    }
}