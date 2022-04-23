package com.example.videostreamingapp.messages

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videostreamingapp.repository.Repos
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class MessageViewModel:ViewModel() {
    private val repository: Repos =Repos()

    fun sendMessageToRoomNode(message: String, currentNode: String): Job {
        return viewModelScope.launch {
            repository.sendMessageToRoomNode(message,currentNode)
        }
    }

    fun fetchMessageFromRoomNode(roomid: String):MutableLiveData<ArrayList<MessageData>> {
        return repository.fetchMessagesFromRootNode(roomid)
    }
}