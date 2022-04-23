package com.example.videostreamingapp.mainscreen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.Location
import android.net.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.videostreamingapp.R
import com.example.videostreamingapp.bottomnavfragment.createroom.CreateRoom
import com.example.videostreamingapp.bottomnavfragment.home.Home
import com.example.videostreamingapp.bottomnavfragment.profile.Profile
import com.example.videostreamingapp.databinding.ActivityMainScreenBinding
import com.example.videostreamingapp.databinding.UpdateDialogBinding
import com.example.videostreamingapp.firebaseDatabaseService
import com.example.videostreamingapp.loginwithgmail.LoginWithGmail
import com.example.videostreamingapp.model.OnlineModel
import com.example.videostreamingapp.ui.CustomProgressDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.BuildConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.net.UnknownHostException


class MainScreenActivity : AppCompatActivity() {

    lateinit var mainBinding: ActivityMainScreenBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101
    private lateinit var currentLocation: Location
    private var lat: String? = null
    private var long: String? = null
    lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
    private lateinit var dialogBinding: UpdateDialogBinding

    companion object {
        var newUrl = ""
        var listOfMember: ArrayList<OnlineModel> = ArrayList()
        const val LATI = "lati"
        const val LONG = "long"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_screen)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        FirebaseMessaging.getInstance().subscribeToTopic("notification_all")
        checkForUpdate()
        val home = Home()
        val profile = Profile()
        val createRoom = CreateRoom()
//        val notification = Notification()
//        val users = AllUsers()

        CoroutineScope(Dispatchers.IO).launch {
            fetchLocation()
        }

        if (firebaseDatabaseService.firebaseAuth.currentUser != null) {
            CoroutineScope(Dispatchers.IO).launch {
                if (intent.extras != null) {
                    val extras = intent.extras
                    newUrl = extras!!.getString(Intent.EXTRA_TEXT).toString()
                    setCurrentFragment(createRoom)
                    mainBinding.bottomNavigation.selectedItemId = R.id.createroom
                } else {
                    setCurrentFragment(home)
                }
            }
        } else {
            startActivity(Intent(this, LoginWithGmail::class.java))
        }

        CoroutineScope(Dispatchers.IO).launch {
            mainBinding.bottomNavigation.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.home -> setCurrentFragment(home)
                    R.id.createroom -> setCurrentFragment(createRoom)
                    R.id.profile -> setCurrentFragment(profile)
                }
                true
            }
        }

        // check internet connection
        checkInternet()
    }

    private fun checkForUpdate() {
        firebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)

        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(OnCompleteListener {
            val latest_version_code: Int = firebaseRemoteConfig.getDouble("App_Version").toInt()

            if (latest_version_code > getCurrentVersion()) {
                launchAlertDialog(latest_version_code)
                Toast.makeText(this, getCurrentVersion().toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun launchAlertDialog(latest_version_code: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val packageName: String = BuildConfig.APPLICATION_ID
        dialogBinding = UpdateDialogBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)
        dialogBinding.whatsNew.text = resources.getString(R.string.Whats_new)+latest_version_code
        dialogBinding.updateBtn.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (e: Exception) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }
//        dialog1.setTitle("App Update Required")
//        dialog1.setMessage("App Recommends that you to update latest version")
//        dialog1.setPositiveButton("Update", (DialogInterface.OnClickListener { dialog, which ->
//            val packageName:String = BuildConfig.APPLICATION_ID
//            try {
//                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
//            }
//            catch (e:Exception){
//                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
//            }
//        }))

        val alertDialog:AlertDialog  = builder.create();
        alertDialog.show()
        alertDialog.setCancelable(false)
    }

    private fun getCurrentVersion(): Int {
        var versionCode = 1

        val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)

        versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode.toInt()
        } else {
            packageInfo.versionCode
        }
        return versionCode
    }

    fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode
            )
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener {
            if (it != null) {
                currentLocation = it
                lat = currentLocation.latitude.toString()
                long = currentLocation.longitude.toString()
                val hashMap = HashMap<String, String>()
                hashMap[LATI] = lat!!
                hashMap[LONG] = long!!
                firebaseDatabaseService.userRef.child(firebaseDatabaseService.firebaseAuth.currentUser!!.uid)
                    .updateChildren(
                        hashMap as Map<String, Any>
                    )
            }
        }

    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            if (requestCode == permissionCode) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation()
                } else {
                    Toast.makeText(this, R.string.provide_permission, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun isNetworkAvailable(context: MainScreenActivity): MutableLiveData<Boolean> {
        var result:MutableLiveData<Boolean> = MutableLiveData()
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
                .isConnected){
            result.postValue(true)
        }else{
            result.postValue(false)
        }
        return result
    }
    fun isInternetAvailable(): MutableLiveData<Boolean> {
        var result:MutableLiveData<Boolean> = MutableLiveData()
        CoroutineScope(Dispatchers.IO).launch {
            val address: InetAddress = InetAddress.getByName("www.google.com")
            if (!address.equals("")){
                result.postValue(true)
            }else{
                result.postValue(false)
            }

        }

        return result
    }
    fun checkInternet(){
        var dilogBox = CustomProgressDialog()
        dilogBox.show(this@MainScreenActivity)
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            // network is available for use
            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                if (dilogBox.dialog.isShowing){
                    dilogBox.dialog.dismiss()
                }
            }

            // Network capabilities have changed for the network
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            }

            // lost network connection
            override fun onLost(network: Network) {
                super.onLost(network)
                dilogBox.show(this@MainScreenActivity,"Internet connection lost!","s")

            }
        }

        val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)

    }

}