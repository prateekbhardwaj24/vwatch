package com.example.videostreamingapp.firebase

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.videostreamingapp.R
import com.example.videostreamingapp.databinding.ActivityRoomBinding
import com.example.videostreamingapp.firebaseDatabaseService
import com.example.videostreamingapp.messages.MessageAdpter
import com.example.videostreamingapp.messages.MessageData
import com.example.videostreamingapp.model.OnlineModel
import com.example.videostreamingapp.ui.OnlineAdapter
import com.example.videostreamingapp.ui.RoomActivity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView


class FirebaseDatabase(private val roomActivity: Context, private val layout: View) {

    var isLoaded: Boolean = false
    var isMaxData: Boolean = false
    var lastNode: String? = ""
    var lastKey: String? = ""

    //for message
    var isLoaded1: Boolean = false
    var isMaxData1: Boolean = false
    var lastNode1: String? = ""
    var lastKey1: String? = ""

    fun getOnlineUsers(
        roomId: String,
        type: String,
        onlineAdapter: OnlineAdapter,
        binding: ActivityRoomBinding
    ) {
        if (type == "video") {
            if (!isMaxData) {
                val query: Query = if (TextUtils.isEmpty(lastNode)) {
                    firebaseDatabaseService.ref.child("CurrentlyPlaying").child(roomId)
                        .child("OnlineViewers").orderByKey().limitToFirst(RoomActivity.ITEM_COUNT)
                } else {
                    firebaseDatabaseService.ref.child("CurrentlyPlaying").child(roomId)
                        .child("OnlineViewers").orderByKey().startAt(lastNode)
                        .limitToFirst(RoomActivity.ITEM_COUNT)
                }
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChildren()) {
                            val list = ArrayList<OnlineModel>()
                            for (s in snapshot.children) {
                                list.add(s.getValue(OnlineModel::class.java)!!)
                                Log.d("hjsdcds", list.size.toString())
                            }
                            val lastKeyQuery: Query =
                                firebaseDatabaseService.ref.child("CurrentlyPlaying").child(roomId)
                                    .child("OnlineViewers").orderByKey().limitToLast(1)
                            lastKeyQuery.addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot1: DataSnapshot) {
                                    for (s in snapshot1.children) {
                                        lastKey = s.key
                                        Log.d("sdjcbjs", lastKey.toString())
                                    }
                                    lastNode = list[list.size - 1].id
                                    if (!lastNode.equals(lastKey)) {
                                        list.removeAt(list.size - 1)
                                        Log.d("sdjcbjs", "if, $lastNode, $lastKey")
                                    } else {
                                        lastNode = "end"
                                        Log.d("sdjcbjs", "else, $lastNode, $lastKey")
                                    }
                                    onlineAdapter.updateLiveRoom(list, roomActivity)
                                    isLoaded = false
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })

                        } else {
                            isLoaded = false
                            isMaxData = true
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
    }

    fun getCountOfViewers(roomId: String, viewsTv: TextView) {
        var views = 0
        firebaseDatabaseService.ref.child("CurrentlyPlaying").child(roomId).child("OnlineViewers")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        views = snapshot.childrenCount.toInt()
                        viewsTv.text = views.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //  TODO("Not yet implemented")
                }

            })

    }

    fun setToast(
        userName: String,
        uniqueName: String,
        uImage: String,
        lati: String,
        long: String,
        context: Context
    ) {
        val name = layout.findViewById<TextView>(R.id.uName)
        val uName = layout.findViewById<TextView>(R.id.userNameTv)
        val image = layout.findViewById<CircleImageView>(R.id.toast_image)
        val distane = layout.findViewById<TextView>(R.id.userDistanceTv)
        val toast = Toast(roomActivity)

        name.text = userName
        uName.text = uniqueName
        getDistanceFromBtoA(lati, long, context).observeForever(Observer {

                distane.text = "$it kms"
            Log.d("fgfhjddjjd","dis -> ${it}")
        })

        Glide.with(roomActivity).load(uImage).into(image)
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.view = layout
        toast.show()
    }

    @SuppressLint("MissingPermission")
    fun getDistanceFromBtoA(lati: String, long: String, context: Context): MutableLiveData<String> {
        var distanceResult: MutableLiveData<String> = MutableLiveData()
        Log.d("fgfhjddjjd","dis -> called")
        if (lati != "") {
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context)
            val task = fusedLocationProviderClient.lastLocation
            task.addOnSuccessListener {
                if (it != null) {
                    val cLat = it.latitude
                    val cLong = it.longitude
                    val results = FloatArray(1)
                    Location.distanceBetween(
                        cLat,
                        cLong,
                        lati.toDouble(),
                        long.toDouble(),
                        results
                    )
                    val distance = results[0]
                    val text = String.format("%.2f", distance)
                    val meter = text.toFloat()
                    val km = meter / 1000
                    val distanceKm = String.format("%.2f", km)
                    // holder.userInfo.setText("$distanceKm km away")
                    Log.d("fgfhjddjjd","dis is -> ${results}")
                    distanceResult.postValue(distanceKm)
//                    val locationCallback: LocationCallback = object : LocationCallback() {
//                        override fun onLocationResult(locationResult: LocationResult) {
//                            val results = FloatArray(1)
//                            Location.distanceBetween(
//                                cLat,
//                                cLong,
//                                lati.toDouble(),
//                                long.toDouble(),
//                                results
//                            )
//                            val distance = results[0]
//                            val text = String.format("%.2f", distance)
//                            val meter = text.toFloat()
//                            val km = meter / 1000
//                            val distanceKm = String.format("%.2f", km)
//                            // holder.userInfo.setText("$distanceKm km away")
//
//                        }
//                    }
//                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
//                    Log.d("fgfhjddjjd","dis -> task ${it.latitude}")
                }
            }
            Log.d("fgfhjddjjd","dis -> if")
        }else{
            Log.d("fgfhjddjjd","dis -> else")
            distanceResult.postValue("")
        }
        return distanceResult
    }

    fun fetchLocation() {


    }

    fun getAllMessage(roomId: String, messageAdapter: MessageAdpter) {
        if (!isMaxData1) {
            val query: Query = if (TextUtils.isEmpty(lastNode1)) {
                firebaseDatabaseService.ref.child("CurrentlyPlaying").child(roomId)
                    .child("Messages").orderByKey().limitToFirst(RoomActivity.MESSAGE_COUNT)
            } else {
                firebaseDatabaseService.ref.child("CurrentlyPlaying").child(roomId)
                    .child("Messages").orderByKey().startAt(lastNode1)
                    .limitToFirst(RoomActivity.MESSAGE_COUNT)
            }
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()) {
                        val list = ArrayList<MessageData>()
                        for (s in snapshot.children) {
                            list.add(s.getValue(MessageData::class.java)!!)
                            Log.d("hjsdcds", list.size.toString())
                        }
                        val lastKeyQuery: Query =
                            firebaseDatabaseService.ref.child("CurrentlyPlaying").child(roomId)
                                .child("Messages").orderByKey().limitToLast(1)
                        lastKeyQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot1: DataSnapshot) {
                                for (s in snapshot1.children) {
                                    lastKey1 = s.key
                                }
                                lastNode1 = list[list.size - 1].time
                                if (!lastNode1.equals(lastKey1)) {
                                    list.removeAt(list.size - 1)
                                } else {
                                    lastNode1 = "end"
                                }
                                messageAdapter.setAllMessages(roomActivity, list)
                                Log.d("jbsbjasx", list.size.toString())
                                isLoaded1 = false
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })

                    } else {
                        isLoaded1 = false
                        isMaxData1 = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    fun getCountOfViewersFromAudio(currentNode: String, viewsTv: TextView) {

        firebaseDatabaseService.musicRef.child("CurrentPlayer").child(currentNode)
            .child("OnlineViewers")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val views = snapshot.childrenCount.toInt()
                        viewsTv.text = views.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //  TODO("Not yet implemented")
                }

            })

    }

}