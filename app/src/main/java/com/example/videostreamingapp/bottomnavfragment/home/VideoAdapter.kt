package com.example.videostreamingapp.bottomnavfragment.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.videostreamingapp.R
import com.example.videostreamingapp.bottomnavfragment.home.publicroom.ItemViewHolder
import com.example.videostreamingapp.model.LiveModel
import com.firebase.ui.database.paging.DatabasePagingOptions
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoAdapter(
    options: DatabasePagingOptions<LiveModel>,
    private val context: Context,
    private val roomType: String
) :
    FirebaseRecyclerPagingAdapter<LiveModel, ItemViewHolder>(options) {
    private val viewModel = HomeViewModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_thumnail_layout, parent, false)
        return ItemViewHolder(root)
    }

    override fun onBindViewHolder(viewHolder: ItemViewHolder, position: Int, model: LiveModel) {
        viewHolder.bind(model, context,roomType,position)
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.deleteVideo(model)
        }
    }
}