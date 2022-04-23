package com.example.videostreamingapp.messages

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videostreamingapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MessageActivity : AppCompatActivity() {
    lateinit var messageRv: RecyclerView
    lateinit var messageBox: EditText
    lateinit var messageSendBtn: FloatingActionButton
    lateinit var messageViewModel: MessageViewModel
    lateinit var messageAdpter: MessageAdpter
     var messagList:ArrayList<MessageData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)


        messageViewModel = ViewModelProvider(
            this
        ).get(MessageViewModel::class.java)

        messageRv = findViewById(R.id.messageRv)
        messageBox = findViewById(R.id.messageTypeEditText)
        messageSendBtn = findViewById(R.id.messageSendBtn)

        //send message in room node
//        messageSendBtn.setOnClickListener {
//            if (messageBox.text.toString().trim().isNotBlank()){
//                sendMessageToRoomNode(messageBox.text.toString())
//
//            }
//        }

       // messageRv.layoutManager = LinearLayoutManager(this)
        val mLayoutManager = LinearLayoutManager(this)
            //  mLayoutManager.reverseLayout = true
        mLayoutManager.stackFromEnd = true
        messageRv.setLayoutManager(mLayoutManager)
        messageAdpter = MessageAdpter()

        //get messages from room node

        getAllMessagesFromRoomNode("roomId")


    }
//
//    private fun sendMessageToRoomNode(message: String) {
//       messageViewModel.sendMessageToRoomNode(message, currentNode).observe(this, Observer {
//           if (it){
//               Log.d("checkValue123","sent $message")
//               messageBox.text.clear()
//              // getAllMessagesFromRoomNode("roomId")
//              // messageAdpter.notifyDataSetChanged()
//           }else{
//               Log.d("checkValue123","not your sent $message")
//
//           }
//       })
//    }

    private fun getAllMessagesFromRoomNode(roomid: String) {

        messageViewModel.fetchMessageFromRoomNode(roomid).observe(this, Observer {
            list->
            list?.let {
                messagelist->

                messagList = messagelist

                messageAdpter.setAllMessages(this,messagelist)
                messageRv.adapter = messageAdpter

            }
        })
    }
}