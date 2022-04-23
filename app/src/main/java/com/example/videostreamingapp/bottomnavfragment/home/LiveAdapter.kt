package com.example.videostreamingapp.bottomnavfragment.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videostreamingapp.R
import com.example.videostreamingapp.mainscreen.MainScreenActivity
import com.example.videostreamingapp.model.LiveModel
import com.example.videostreamingapp.ui.RoomActivity
import com.google.android.youtube.player.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LiveAdapter(private val viewModel: HomeViewModel, private val context: Context) :
    RecyclerView.Adapter<LiveAdapter.myViewHolder>() {
    var arrayList:ArrayList<LiveModel>

    init {
        this.arrayList = ArrayList()
    }
    fun updateLiveRoom(updateList: ArrayList<LiveModel>) {
        val init:Int = arrayList.size
        arrayList.addAll(updateList)
//        notifyDataSetChanged()
        notifyItemRangeChanged(init, updateList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_thumnail_layout, parent, false)
        return myViewHolder(root)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder.bind(arrayList[position])
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            data: LiveModel
        ) {
            itemView.findViewById<TextView>(R.id.viewsTv).text = data.views
            itemView.findViewById<TextView>(R.id.durationTv).text = data.duration
            itemView.findViewById<TextView>(R.id.titleTv).text = data.title
            itemView.findViewById<TextView>(R.id.categoryTv).text = data.category
            val video_thumbnail = itemView.findViewById<ImageView>(R.id.video_thumbnail)

            Glide.with(context).load(data.video_thumbnail).into(video_thumbnail)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.deleteVideo(data)
            }

            itemView.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, RoomActivity::class.java)
                intent.putExtra("url", data.videoId)
                intent.putExtra("isLive", "true")
                intent.putExtra("nodeId", data.currentDuration)
                intent.putExtra("adminId", data.adminId)
                intent.putExtra("title", data.title)
                intent.putExtra("video_time", data.duration)
               MainScreenActivity.listOfMember = data.members
//                intent.putExtra("videoDuration", data.currentDuration)
                context.startActivity(intent)
            })

        }
    }

}