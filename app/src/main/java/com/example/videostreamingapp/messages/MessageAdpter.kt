package com.example.videostreamingapp.messages

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.videostreamingapp.R
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdpter : RecyclerView.Adapter<MessageAdpter.MyViewHolder>() {
    private var allMessageList: ArrayList<MessageData> = ArrayList()
    private lateinit var context: Context
    //  private lateinit var messageViewModel: MessageViewModel

    fun setAllMessages(
        context: Context,
        messagelist: ArrayList<MessageData>,

        ) {
        this.allMessageList = messagelist
        this.context = context
        // this.messageViewModel = messageViewModel
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var perosnImage: CircleImageView
        var messageText: TextView
        var senderName: TextView
        var has_l_j: TextView

        init {
            perosnImage = itemView.findViewById(R.id.messageImg)
            messageText = itemView.findViewById(R.id.messageTextView)
            senderName = itemView.findViewById(R.id.messageSenderName)
            has_l_j = itemView.findViewById(R.id.has_j_l)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.message_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = allMessageList.get(position)
        holder.has_l_j.visibility = View.GONE
        holder.senderName.text = data.sendName
        Glide.with(context).load(data.sendImage).into(holder.perosnImage)
        when (data.type) {
            joined -> {
                holder.has_l_j.visibility = View.VISIBLE
                holder.has_l_j.text = context.resources.getString(R.string.has_joined)
            }
            left -> {
                holder.has_l_j.visibility = View.VISIBLE
                holder.has_l_j.text = context.resources.getString(R.string.has_left)
            }
            else -> {
                holder.messageText.text = data.message
                holder.has_l_j.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return allMessageList.size
    }

    companion object{
        private const val joined = "joined"
        private const val left = "left"
    }

}