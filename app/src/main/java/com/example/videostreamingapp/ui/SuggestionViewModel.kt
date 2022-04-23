package com.example.videostreamingapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videostreamingapp.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SuggestionViewModel : ViewModel() {
    companion object{
        var makeSuggestionRequestToDb: MutableLiveData<Boolean> = MutableLiveData()
    }
    private val repository: Repository = Repository()

    var _makeSuggestionRequestToDb = makeSuggestionRequestToDb as LiveData<Boolean>
    fun makeSuggestionRequestToDb(suggestionMessege: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.makeSuggestionRequestToDb(suggestionMessege)
            Log.d("checkFrT","check two : "+makeSuggestionRequestToDb.value)
        }
    }

}