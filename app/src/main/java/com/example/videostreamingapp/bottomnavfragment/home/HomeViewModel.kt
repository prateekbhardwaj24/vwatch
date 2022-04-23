package com.example.videostreamingapp.bottomnavfragment.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.videostreamingapp.model.LiveModel
import com.example.videostreamingapp.repo.Repository

class HomeViewModel : ViewModel() {
    private val repository = Repository()

    fun getLiveRooms(): MutableLiveData<ArrayList<LiveModel>> {
        return repository.getLiveRooms()
    }

    suspend fun deleteVideo(data: LiveModel){
        repository.deleteVideo(data)
    }


}