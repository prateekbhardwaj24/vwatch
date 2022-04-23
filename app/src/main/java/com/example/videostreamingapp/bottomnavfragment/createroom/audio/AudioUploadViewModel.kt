package com.example.videostreamingapp.bottomnavfragment.createroom.audio

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.Modals.AudioModal
import com.example.videostreamingapp.repo.Repository

class AudioUploadViewModel : ViewModel() {
    private val repository = Repository()

//    fun uploadSongToStorage(musicUri: Uri): MutableLiveData<Uri> {
//return repository.uploadSongData(musicUri, context)
//    }

//    fun uploadSongDetailsInRTDB(dataModal: AudioModal, uri: Uri):MutableLiveData<Boolean> {
//       //return repository.uploadSongDetails(dataModal,uri)
//    }

}