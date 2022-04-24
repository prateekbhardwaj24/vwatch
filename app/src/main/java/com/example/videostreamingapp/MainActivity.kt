package com.example.videostreamingapp

import android.content.Intent
import android.location.Location
import android.net.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.videostreamingapp.loginwithgmail.LoginWithGmail
import com.example.videostreamingapp.mainscreen.MainScreenActivity
import com.example.videostreamingapp.ui.CustomProgressDialog
import com.example.videostreamingapp.ui.RoomActivity
import com.example.videostreamingapp.username.UserNameActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101
    private lateinit var currentLocation: Location
    private var lat: String? = null
    private var long: String? = null
    private var isDialogInitialized: Boolean = false
    private var progressDialog: CustomProgressDialog = CustomProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // progressDialog.show(this, "Please wait...")
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        Log.d(
            "timehai",
            "${ServerValue.TIMESTAMP.values} and substraction by 5 ${ServerValue.TIMESTAMP - 5}"
        )
        getShareableLink()
        if (user != null) {
            val checkUserExistRTDB =
                firebaseDatabaseService.userRef.child(firebaseDatabaseService.firebaseAuth.currentUser!!.uid)
            checkUserExistRTDB.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        val mainActivityIntent =
                            Intent(this@MainActivity, MainScreenActivity::class.java)
                        startActivity(mainActivityIntent)
                        finish()
                    } else {
                        val loginActivityIntent =
                            Intent(this@MainActivity, UserNameActivity::class.java)
                        startActivity(loginActivityIntent)
                        finish()
                    }
                    //  progressDialog.dialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
//                    progressDialog.dialog.dismiss()
                }

            })

        } else {
            // progressDialog.dialog.dismiss()
            val loginActivityIntent = Intent(this, LoginWithGmail::class.java)
            startActivity(loginActivityIntent)
            finish()
        }
       // checkInternet()
    }

    private fun getShareableLink() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null

                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }

                deepLink?.let { uri ->
                    val path = uri.toString().substring(deepLink.toString().lastIndexOf("/") + 1)
                    when {
                        uri.toString().contains("room") -> {
                            // In my case, the ID is an Integer
                            //   Log.d("gfhjd","room contains")
                            val roomId = path
                            getDataOfVideoFromNode(roomId)
                            // Call your API or DB to get the post with the ID [postId]
                            // and open the required screen here.
                        }

                    }
                }
            }.addOnFailureListener {
                // This lambda will be triggered when there is a failure.
                // Handle
                /*  Log.d(TAG, "handleIncomingDeepLinks: ${it.message}")*/
            }
    }


    private fun getDataOfVideoFromNode(roomId: String) {
        firebaseDatabaseService.ref.child(firebaseDatabaseService.CURRENT_ROOM).child(roomId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val title = snapshot.child("title").value.toString()
                        val url = snapshot.child("videoId").value.toString()
                        val duration = snapshot.child("duration").value.toString()
                        val adminId = snapshot.child("adminId").value.toString()
                        val intent = Intent(this@MainActivity, RoomActivity::class.java)
                        intent.putExtra("url", url)
                        intent.putExtra("isLive", "true")
                        intent.putExtra("nodeId", roomId)
                        intent.putExtra("adminId", adminId)
                        intent.putExtra("title", title)
                        intent.putExtra("video_time", duration)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    fun checkInternet() {
        var dilogBox = CustomProgressDialog()

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            // network is available for use
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                if (isDialogInitialized) {
                    if (dilogBox.dialog.isShowing) {
                        dilogBox.dialog.dismiss()
                    }
                }
            }

            // Network capabilities have changed for the network
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val unmetered =
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            }

            // lost network connection
            override fun onLost(network: Network) {
                super.onLost(network)
                dilogBox.show(this@MainActivity, "Internet connection lost!", "s")
                isDialogInitialized = true
            }
        }

        val connectivityManager =
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }


}