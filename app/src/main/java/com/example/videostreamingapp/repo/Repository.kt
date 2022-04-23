package com.example.videostreamingapp.repo

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.musicplayer.Modals.AudioModal

import com.example.videostreamingapp.fcmpushnotification.SendPushNotification
import com.example.videostreamingapp.firebaseDatabaseService

import com.example.videostreamingapp.model.LiveModel
import com.example.videostreamingapp.model.OnlineModel
import com.example.videostreamingapp.ui.SuggestionViewModel
import com.example.videostreamingapp.username.UserModel
import com.example.videostreamingapp.utils.Converter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.apache.commons.net.ntp.NTPUDPClient
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.InetAddress

class Repository {
    var converter: Converter = Converter()
    fun getLiveRooms(): MutableLiveData<ArrayList<LiveModel>> {
        val livedata: MutableLiveData<ArrayList<LiveModel>> = MutableLiveData()
        val tempList1: ArrayList<LiveModel> = ArrayList()
        firebaseDatabaseService.ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    tempList1.clear()
                    val tempList: ArrayList<LiveModel> = ArrayList()
                    for (liveSnap in snapshot.children) {
                        val data = liveSnap.getValue(LiveModel::class.java)
                        data?.let {
                            tempList.add(it)
                            livedata.value = tempList
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        return livedata
    }

    suspend fun deleteVideo(data: LiveModel) {
        //  val currentTime = converter.getCurrentTime()
        val job = CoroutineScope(Dispatchers.IO).async {
            NTPUDPClient().getTime(InetAddress.getByName("time-a.nist.gov")).message.receiveTimeStamp.time
        }

        val cTime = data.currentDuration!!.toLong()
        val difTime = job.await() - cTime
        val currentDuration = data.videoUploadTime!!.toLong()
        if (difTime > currentDuration) {
            val await = if (data.type.equals("Private")) {
                firebaseDatabaseService.ref.child("PrivateRooms")
                    .child(firebaseDatabaseService.firebaseAuth.currentUser!!.uid)
                    .child("AllRooms").child(data.currentDuration!!)
            } else {
                firebaseDatabaseService.ref.child("PublicRooms").child(data.currentDuration!!)

            }
            var await1 =
                firebaseDatabaseService.ref.child("CurrentlyPlaying").child(data.currentDuration!!)

            await.ref.removeValue()
            await1.ref.removeValue()

        }

    }



    fun getNameFromId(id: String): MutableLiveData<String> {
        var name: MutableLiveData<String> = MutableLiveData()

        val node = firebaseDatabaseService.userRef.child(id).child("name")
        node.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    name.postValue(snapshot.value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //  TODO("Not yet implemented")
            }


        })

        return name
    }

    fun fetchUserData(): MutableLiveData<UserModel> {
        var result: MutableLiveData<UserModel> = MutableLiveData()
        CoroutineScope(Dispatchers.IO).launch {
            firebaseDatabaseService.userRef.child(firebaseDatabaseService.firebaseAuth.currentUser!!.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        result.postValue(snapshot.getValue(UserModel::class.java))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }
        return result
    }

    fun updateNewUName(newName: String): MutableLiveData<Boolean> {
        var result: MutableLiveData<Boolean> = MutableLiveData()
        CoroutineScope(Dispatchers.IO).launch {
            firebaseDatabaseService.userRef.child(firebaseDatabaseService.firebaseAuth.currentUser!!.uid)
                .child("uname").setValue(newName).addOnCompleteListener {
                    result.postValue(true)
                }
        }
        return result
    }

    suspend fun makeSuggestionRequestToDb(suggestionMessage: String) {
        var result:MutableLiveData<Boolean> = MutableLiveData()
        val job = CoroutineScope(Dispatchers.IO).async {
            NTPUDPClient().getTime(InetAddress.getByName("time-a.nist.gov")).message.receiveTimeStamp.time
        }

            val ref = firebaseDatabaseService.suggestionRef.child(job.await().toString())
            val hashmap: HashMap<String, String> = HashMap()
            hashmap.put("suggestionby", firebaseDatabaseService.firebaseAuth.currentUser!!.uid)
            hashmap.put("suggestionMessage", suggestionMessage)
            ref.setValue(hashmap).addOnSuccessListener {
                SuggestionViewModel.makeSuggestionRequestToDb.postValue(true)
                Log.d("checkFrT","check one : "+result.value.toString())
            }
    }

}

