package com.example.videostreamingapp.bottomnavfragment.createroom

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videostreamingapp.model.OnlineModel
import com.example.videostreamingapp.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateRoomViewModel : ViewModel() {
    val repository = Repository()


    fun getNameFromId(id: String):MutableLiveData<String> {
        return repository.getNameFromId(id)
    }

}