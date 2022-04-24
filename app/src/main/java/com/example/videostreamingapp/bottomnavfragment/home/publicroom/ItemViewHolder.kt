package com.example.videostreamingapp.bottomnavfragment.home.publicroom

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videostreamingapp.R
import com.example.videostreamingapp.bottomnavfragment.home.HomeViewModel
import com.example.videostreamingapp.firebaseDatabaseService

import com.example.videostreamingapp.mainscreen.MainScreenActivity
import com.example.videostreamingapp.model.LiveModel
import com.example.videostreamingapp.ui.BackgroundColorChanger
import com.example.videostreamingapp.ui.RoomActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val viewModel = HomeViewModel()
    fun bind(
        data: LiveModel, context: Context, roomType: String, position: Int
    ) {
        itemView.findViewById<TextView>(R.id.viewsTv).text = data.views
        itemView.findViewById<TextView>(R.id.durationTv).text = data.duration
        itemView.findViewById<TextView>(R.id.titleTv).text = data.title
        itemView.findViewById<TextView>(R.id.categoryTv).text = data.category
        itemView.findViewById<TextView>(R.id.uploaded_by).text = data.category
        itemView.findViewById<TextView>(R.id.categoryTv).text = data.category

        data.adminId?.let {
            nameByAdminId(it).observeForever(Observer { mapData->
                mapData?.let {
                    itemView.findViewById<TextView>(R.id.uploaded_by).text = mapData["uname"]
                    Glide.with(context).load(mapData["uimg"]).into(itemView.findViewById<CircleImageView>(R.id.user_img))
                }
            })
        }

       // setBackgroundColor(position,changeBackLayout,context)
        val video_thumbnail = itemView.findViewById<ImageView>(R.id.video_thumbnail)

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.deleteVideo(data)
        }
        Glide.with(context).load(data.video_thumbnail).into(video_thumbnail)

        itemView.setOnClickListener(View.OnClickListener {

            val intent = Intent(context, RoomActivity::class.java)
            intent.putExtra("url", data.videoId)
            intent.putExtra("isLive", "true")
            intent.putExtra("nodeId", data.currentDuration)
            intent.putExtra("adminId", data.adminId)
            intent.putExtra("title", data.title)
            intent.putExtra("video_time", data.duration)
            intent.putExtra("typeOfRoom", roomType)
            MainScreenActivity.listOfMember = data.members
//                intent.putExtra("videoDuration", data.currentDuration)
            context.startActivity(intent)
        })

    }

    private fun setBackgroundColor(
        position: Int,
        changeBackLayout: RelativeLayout,
        context: Context
    ) {
        when {
            position%5 == 0 -> {
                BackgroundColorChanger().changeColor(changeBackLayout,context,0)
            }
            position%5 == 1 -> {
                BackgroundColorChanger().changeColor(changeBackLayout,context,1)
            }
            position%5 == 2 -> {
                BackgroundColorChanger().changeColor(changeBackLayout,context,2)
            }
            position%5 == 3 -> {
                BackgroundColorChanger().changeColor(changeBackLayout,context,3)
            }
            position%5 == 4 -> {
                BackgroundColorChanger().changeColor(changeBackLayout,context,4)
            }
        }
    }

    private fun nameByAdminId(adminId: String): MutableLiveData<HashMap<String, String>> {
        val str:MutableLiveData<HashMap<String,String>> = MutableLiveData()
        firebaseDatabaseService.userRef.child(adminId).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var mapData:HashMap<String,String> = HashMap()
                mapData["name"] = snapshot.child("name").value.toString()
                mapData["uname"] = snapshot.child("uname").value.toString()
                mapData["uimg"] = snapshot.child("imageUri").value.toString()
                str.postValue(mapData)
            }

            override fun onCancelled(error: DatabaseError) {
               // TODO("Not yet implemented")
            }

        })
        return str
    }
}