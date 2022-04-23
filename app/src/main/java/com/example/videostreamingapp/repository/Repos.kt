package com.example.videostreamingapp.repository

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData

import com.example.videostreamingapp.firebaseDatabaseService

import com.example.videostreamingapp.messages.MessageData
import com.example.videostreamingapp.username.UserModel
import com.example.videostreamingapp.utils.Converter
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.apache.commons.net.ntp.NTPUDPClient
import java.net.InetAddress

class Repos {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firebaseDatabse: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var refNode = Firebase.database.getReference("Users")
    private val messageRef = Firebase.database.getReference("LiveRoom")
    var usersList: ArrayList<UserModel> = ArrayList()

    var isMaxData: Boolean = false
    var isMaxDataConnected: Boolean = false
    var totalItem = 0
    var lastVisibleItem = 0
    var lastKey: String? = ""
    var lastNode: String? = ""
    var lastKeyConnected: String? = ""
    var lastNodeConnected: String? = ""
var converter = Converter()
    companion object {
        var isLoaded: Boolean = false
        var isLoadedConected: Boolean = false
        var isLoadedRequest: Boolean = false
    }

    fun firebaseSignInWithGmail(googleAuthCredential: AuthCredential): MutableLiveData<Boolean> {
        val authenticateUserLiveData: MutableLiveData<Boolean> = MutableLiveData()

        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                var isNewUser = authTask.result?.additionalUserInfo?.isNewUser
                val firebaseUser: FirebaseUser? = firebaseAuth.currentUser


                if (firebaseUser != null) {
//                    val uid = firebaseUser.uid
//                    val name = firebaseUser.displayName
//                    val email = firebaseUser.email
//                    val user = User(uid = uid, name = name, email = email)
//                    user.isNew = isNewUser
                    authenticateUserLiveData.value = true
                    Log.d("123check", "uidname ifff ->>>> $isNewUser")
                } else {
                    Log.d("123check", "uidname elsee2")
                    authenticateUserLiveData.value = false
                }
            }
        }
        return authenticateUserLiveData

    }

    fun insertUserDataInFirebase(userName: String, gender: String): MutableLiveData<Boolean> {
        var token: String = ""

        val res: MutableLiveData<Boolean> = MutableLiveData()
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        val uid = firebaseUser!!.uid
        val name = firebaseUser.displayName
        val email = firebaseUser.email
        val uname = userName

        val imageUri1 = firebaseUser.photoUrl.toString()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {
            if (!it.isSuccessful) {
                return@OnCompleteListener
            }

            token = it.result
            val user = UserModel(
                uid = uid,
                name = name,
                email = email,
                uname = uname,
                imageUri = imageUri1,
                gender = gender,
                lati = "",
                long = "",
                token = token
            )
            refNode.child(uid).setValue(user).addOnCompleteListener {
                Log.d("123check", "   insertedddd ")
                res.value = true
            }
        })

        // refNode.child(uid).setValue(user).addOnCompleteListener(object OnC)


//        refNode.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//
//                Log.d("123check", "   inserted ")
//                res.value = true
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("123check", "  not inserted ")
//                res.value = false
//            }
//
//        })
        Log.d("123check", "  not entered $imageUri1")
        return res
    }

    fun checkUserType(): MutableLiveData<Boolean> {
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        val res: MutableLiveData<Boolean> = MutableLiveData()

        refNode.child(firebaseUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        res.value = false
                    } else {
                        res.value = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("123check", "check canceled")
                }

            })
        return res
    }


suspend fun sendMessageToRoomNode(message: String, currentNode: String): MutableLiveData<Boolean> {
    val result: MutableLiveData<Boolean> = MutableLiveData()

    val job = CoroutineScope(Dispatchers.IO).async {
        NTPUDPClient().getTime(InetAddress.getByName("time-a.nist.gov")).message.receiveTimeStamp.time
    }
        val data = MessageData(
            message,
            job.await().toString(),
            firebaseAuth.currentUser!!.uid,
            firebaseAuth.currentUser!!.displayName.toString(),
            firebaseAuth.currentUser!!.photoUrl.toString()
        )
        CoroutineScope(Dispatchers.IO).launch {
        firebaseDatabaseService.ref.child(currentNode).child(job.await().toString()).setValue(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.postValue(true)
                }

            }
    }


    return result
}

fun fetchMessagesFromRootNode(roomid: String): MutableLiveData<ArrayList<MessageData>> {
    val result: MutableLiveData<ArrayList<MessageData>> = MutableLiveData()
    val messagList: ArrayList<MessageData> = ArrayList()
    firebaseDatabaseService.ref.child(roomid).child(firebaseDatabaseService.MESSAGES)
        .addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()) {
                    val userData = snapshot.getValue(MessageData::class.java)
                    userData?.let {
                        messagList.add(it)
                        result.postValue(messagList)
                    }

                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    return result
}

}