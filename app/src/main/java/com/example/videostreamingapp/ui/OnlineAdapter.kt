package com.example.videostreamingapp.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.videostreamingapp.R
import com.example.videostreamingapp.firebase.FirebaseDatabase
import com.example.videostreamingapp.firebaseDatabaseService
import com.example.videostreamingapp.model.OnlineModel
import com.example.videostreamingapp.ui.OnlineAdapter.myViewHolder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class OnlineAdapter(private val firebaseService: FirebaseDatabase) :
    RecyclerView.Adapter<myViewHolder>() {
    private lateinit var context: Context
    var arrayList: ArrayList<OnlineModel>
    private val permissionCode = 101

    init {
        this.arrayList = ArrayList()
    }

    fun updateLiveRoom(updateList: ArrayList<OnlineModel>, context: Context) {
        val init: Int = arrayList.size
        this.context = context
        arrayList.addAll(updateList)
        notifyDataSetChanged()
        Log.d("jnscdnjs", arrayList.size.toString())
    }

    fun deleteOnlineUser(model: OnlineModel) {
        arrayList.remove(model)
        notifyDataSetChanged()
    }

    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: OnlineModel, position: Int) {
            setUserData(data.id, itemView)
            itemView.setOnClickListener {
                showCustomToast(data.id, itemView)
            }
        }
    }

    private fun showCustomToast(id: String?, itemView: View) {
        if (id != null) {
            firebaseDatabaseService.userRef.child(id)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val name = snapshot.child("name").value.toString()
                            val uniqueName = snapshot.child("uname").value.toString()
                            val imageUrl = snapshot.child("imageUri").value.toString()
                            val lati = snapshot.child("lati").value.toString()
                            val long = snapshot.child("long").value.toString()
                            if (checkGpsStatus()) {
                                firebaseService.setToast(
                                    name,
                                    uniqueName,
                                    imageUrl,
                                    lati,
                                    long,
                                    context
                                )
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }
    }

    private fun checkLocaPerm(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101
            )
            false
        } else {
            true
        }
    }

    private fun checkGpsStatus(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return if (gpsStatus) {
            checkLocaPerm()
        } else {
            openLocaSetting()
        }
    }

    private fun openLocaSetting(): Boolean {
        val intent1 = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent1)
        return false
    }

    private fun setUserData(id: String?, itemView: View) {
        if (id != null) {
            firebaseDatabaseService.userRef.child(id)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val name = snapshot.child("name").value.toString()
                            val imageUrl = snapshot.child("imageUri").value.toString()
                            itemView.findViewById<TextView>(R.id.userNameTv).text = name
                            val userImage = itemView.findViewById<CircleImageView>(R.id.userIv)
                            Picasso.get().load(imageUrl).into(userImage)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }
        if (id == RoomActivity.adminId) {
            itemView.findViewById<ImageView>(R.id.crown).visibility = View.VISIBLE
            val params1 = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
//            val params2 = RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT
//            )
            params1.setMargins(0, 0, 0, 0)
            // params2.setMargins(22, 4, 0, 0)
            //  itemView.findViewById<ImageView>(R.id.userIv).layoutParams = params1
            // itemView.findViewById<TextView>(R.id.userNameTv).layoutParams = params2
        } else {
            itemView.findViewById<ImageView>(R.id.crown).visibility = View.GONE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.online_layout, parent, false)
        return myViewHolder(root)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder.bind(arrayList[position], position)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}