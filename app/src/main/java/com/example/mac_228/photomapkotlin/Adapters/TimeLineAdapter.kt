package com.example.mac_228.photomapkotlin.Adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.mac_228.photomapkotlin.Activity.PhotoDetailsActivity
import com.example.mac_228.photomapkotlin.Models.TimeLineDataModel
import com.example.mac_228.photomapkotlin.R


class TimeLineAdapter(private val mList: List<TimeLineDataModel>, private val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SECTION_TYPE = 1
    private val SECTION_DATA = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val view: View

        when (viewType) {
            SECTION_TYPE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.time_line_recycler_sections, parent, false)
                return SectionViewHolder(view)
            }
            SECTION_DATA -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.time_line_recycler_data, parent, false)
                return DataViewHolder(view)
            }
        }
        return null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = mList[position]
        if (model != null) {
            if (model.sectionName == null) {
                (holder as DataViewHolder)
                holder.mTitle.text = model.data?.mText
                holder.mDate.text = model.data?.mTime
                holder.mType.text = model.data?.mType
                holder.mImage.setImageURI(model.data?.mUri)
                holder.mLayout.setOnClickListener {
                    val intent = Intent(mContext, PhotoDetailsActivity::class.java)
                    intent.putExtra(PhotoDetailsActivity.TAG, PhotoDetailsActivity.EXISTING_PHOTO)
                    intent.putExtra(PhotoDetailsActivity.EXISTING_PHOTO_ID, model.data?.mId.toString())
                    mContext.startActivity(intent)
                }
            } else {
                (holder as SectionViewHolder).mTitle.text = model.sectionName
            }

        }
    }

    override fun getItemCount(): Int {
        if (mList == null)
            return 0
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (mList != null) {

            val model = mList[position]

            if (model.sectionName == null) {
                return SECTION_DATA
            } else {
                return SECTION_TYPE
            }

        }
        return -1
    }

    class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTitle: TextView = itemView.findViewById(R.id.textViewTimeLineSection) as TextView

    }

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTitle: TextView = itemView.findViewById(R.id.textViewTextTimeLine) as TextView
        val mDate: TextView = itemView.findViewById(R.id.textViewDateTimeLine) as TextView
        val mType: TextView = itemView.findViewById(R.id.textViewTypeTimeLine) as TextView
        val mImage: ImageView = itemView.findViewById(R.id.imageViewTimeLine) as ImageView
        val mLayout: LinearLayout = itemView.findViewById(R.id.timeLineLayout) as LinearLayout

    }
}