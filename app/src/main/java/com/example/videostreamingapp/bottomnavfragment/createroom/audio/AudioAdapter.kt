package com.example.videostreamingapp.bottomnavfragment.createroom.audio

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.Modals.AudioModal
import com.example.videostreamingapp.R
import kotlin.collections.ArrayList

class AudioAdapter : RecyclerView.Adapter<AudioAdapter.MyViewHolder>() {

    private lateinit var context: Context
    private var allSongList: ArrayList<AudioModal> = ArrayList()
    private lateinit var viewModel: AudioUploadViewModel
    private lateinit var lifeOwner: LifecycleOwner


    fun getSongs(
        context: Context,
        songList: ArrayList<AudioModal>,
        viewModel: AudioUploadViewModel
    ) {
        allSongList = songList
        this.context = context
        this.viewModel = viewModel
        notifyDataSetChanged()
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var songName: TextView
        var songPath: TextView
        var songIcon: ImageView


        init {
            songName = itemView.findViewById(R.id.folderName)
            songPath = itemView.findViewById(R.id.folderAddress)
            songIcon = itemView.findViewById(R.id.iconImg)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.all_folder_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var dataModal = allSongList.get(position)
        holder.songName.text = dataModal.aName
        holder.songPath.text = dataModal.aArtist
        Glide.with(context).load(dataModal.audioIcon).apply(
            RequestOptions().placeholder(R.drawable.ic_music_icon).centerCrop()
        ).into(holder.songIcon)

        holder.itemView.setOnClickListener {
          //  uploadSongDataToDB(dataModal.musicUri, allSongList.get(position))
        }

    }

//    private fun uploadSongDataToDB(musicUri: Uri, dataModal: AudioModal) {
//        viewModel.uploadSongToStorage(musicUri).observeForever(Observer {
//            if (it != null) {
//                viewModel.uploadSongDetailsInRTDB(dataModal,it).observeForever(Observer { data ->
//                    if (data) {
//                        Toast.makeText(context, data.toString(), Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(context, data.toString(), Toast.LENGTH_SHORT).show()
//                    }
//                })
//                Toast.makeText(context, "in: " + it.toString(), Toast.LENGTH_SHORT).show()
//
//            } else {
//                Toast.makeText(context, "out: " + it.toString(), Toast.LENGTH_SHORT).show()
//            }
//        })
//
//
//    }

    override fun getItemCount(): Int {
        return allSongList.size
    }


}