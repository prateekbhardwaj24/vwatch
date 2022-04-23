package com.example.videostreamingapp.loginwithgmail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.videostreamingapp.repository.Repos

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential

class LoginViewModel():ViewModel() {
    private val repository: Repos = Repos()
    private lateinit var googleSignClient: GoogleSignInClient

     var _authenticateUserLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val authenticateUserLiveData: LiveData<Boolean> get() = _authenticateUserLiveData

    fun signInWithGoogle(googleAuthCredential: AuthCredential):MutableLiveData<Boolean> {
        return repository.firebaseSignInWithGmail(googleAuthCredential)
    }
    fun insertUserData(userName: String, gender: String):MutableLiveData<Boolean> {
        return repository.insertUserDataInFirebase(userName,gender)
    }
    fun checkUserOldNew():MutableLiveData<Boolean> {
        return repository.checkUserType()
    }



}