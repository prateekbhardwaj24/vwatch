package com.example.videostreamingapp.bottomnavfragment.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.videostreamingapp.repo.Repository
import com.example.videostreamingapp.username.UserModel

class ProfileViewModel : ViewModel() {
    private val repository = Repository()

    fun fetchCurrentProfileData():MutableLiveData<UserModel> {
return repository.fetchUserData()
    }

    fun updateNewUName(newName: String):MutableLiveData<Boolean> {
        return repository.updateNewUName(newName)
    }

}