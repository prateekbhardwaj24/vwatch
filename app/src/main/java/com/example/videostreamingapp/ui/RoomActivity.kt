package com.example.videostreamingapp.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.*
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.videostreamingapp.R
import com.example.videostreamingapp.databinding.ActivityRoomBinding
import com.example.videostreamingapp.fcmpushnotification.SendPushNotification
import com.example.videostreamingapp.firebase.FirebaseDatabase
import com.example.videostreamingapp.firebase.HomeFirebaseDao
import com.example.videostreamingapp.firebaseDatabaseService
import com.example.videostreamingapp.mainscreen.MainScreenActivity
import com.example.videostreamingapp.messages.MessageAdpter
import com.example.videostreamingapp.messages.MessageData
import com.example.videostreamingapp.model.LiveModel
import com.example.videostreamingapp.model.OnlineModel
import com.example.videostreamingapp.utils.Converter
import com.example.videostreamingapp.utils.ShareIntent
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.apache.commons.net.ntp.NTPUDPClient
import org.json.JSONException
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL


class RoomActivity : YouTubeBaseActivity() {

    private lateinit var binding: ActivityRoomBinding
    private lateinit var url: String
    private lateinit var currentNode: String
    private lateinit var videoId: String

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sheetDialog: BottomSheetDialog
    private lateinit var shareIntent: ShareIntent
    lateinit var messageAdpter: MessageAdpter
    private lateinit var onlineAdapter: OnlineAdapter
    private lateinit var firebaseService: FirebaseDatabase
    var messagList: ArrayList<MessageData> = ArrayList()
    lateinit var currentTime: String
    private var isPlay: Int = 0
    private lateinit var layout: View
    private lateinit var requestQueue: RequestQueue
    private lateinit var videoTitle: String
    private var list: ArrayList<OnlineModel> = ArrayList()
    private lateinit var converter: Converter
    var typeOfRoom = "Public"
    private var key: String? = null
    private lateinit var firebaseDao: HomeFirebaseDao
    private var isLoading = false
    private lateinit var sendPushNotification: SendPushNotification
    var isMaxData: Boolean = false
    var lastKey: String? = ""
    var lastNode: String? = ""

    init {
        converter = Converter()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_room)

        sendPushNotification = SendPushNotification()
        binding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar(binding.toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        binding.onlineRecyclerView.layoutManager =
            LinearLayoutManager(
                this@RoomActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        requestQueue = Volley.newRequestQueue(this@RoomActivity)
        //   converter.getCurrentTime1()
//        converter._time.observeForever(Observer {
//            currentTime = it.toString()
//        })
        val time = CoroutineScope(Dispatchers.IO).async {
            NTPUDPClient().getTime(InetAddress.getByName("time-a.nist.gov")).message.receiveTimeStamp.time
        }

        sheetDialog = BottomSheetDialog(this@RoomActivity, R.style.BottomSheetStyle)
        val view: View =
            LayoutInflater.from(this@RoomActivity)
                .inflate(R.layout.share_bottom_sheet, findViewById(R.id.sheet))
        shareIntent = ShareIntent(this@RoomActivity, view)
        sheetDialog.setContentView(view)
        binding.shareBtn.setOnClickListener {
            openShareBottomSheet()
        }
        val li = layoutInflater
        layout = li.inflate(R.layout.custom_toast_layout, findViewById(R.id.customtoast))
        firebaseService = FirebaseDatabase(this@RoomActivity, layout)

        setBlur(binding.blurView)
        firebaseAuth = FirebaseAuth.getInstance()
        messageAdpter = MessageAdpter()

        checkInternet()
        if (intent.getStringExtra(URl) != null) {
            if (intent.getStringExtra(IS_LIVE).equals(TRUE)) {
                url = intent.getStringExtra(URl)!!
                val title = intent.getStringExtra(TITLE)!!
                val videoTime = intent.getStringExtra(VIDEO_TIME)!!
                actionBar?.title = title
                actionBar?.subtitle = videoTime
                adminId = intent.getStringExtra(ADMIN_ID)!!
                //  adminID = intent.getStringExtra(ADMIN_ID)!!
                currentNode = intent.getStringExtra(NODE_ID)!!
                list = MainScreenActivity.listOfMember
                videoId = converter.extractYTId(url)!!
                shareIntent.setThumbnail(videoId, binding)
                setAndPlayVideo(binding.ytPlayer)
                fetchChatOfUsers()
                firebaseService.getCountOfViewers(currentNode, binding.viewsTv)

            } else {
                url = intent.getStringExtra(URl)!!
                list = MainScreenActivity.listOfMember
                currentTime = intent.getStringExtra("currentTime")!!
                currentNode = currentTime
                videoId = converter.extractYTId(url)!!
                adminId = firebaseAuth.currentUser!!.uid
                // adminID = intent.getStringExtra(ADMIN_ID)!!
                shareIntent.setThumbnail(videoId, binding)
                setAndPlayVideo(binding.ytPlayer)
                fetchChatOfUsers()
                firebaseService.getCountOfViewers(currentNode, binding.viewsTv)

            }
            typeOfRoom = intent.getStringExtra("typeOfRoom")!!
        }

        onlineAdapter = OnlineAdapter(firebaseService)
        binding.onlineRecyclerView.adapter = onlineAdapter
        firebaseDao = HomeFirebaseDao()
        CoroutineScope(Dispatchers.IO).launch {
            loadOnlineUsers()
        }

        binding.toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainScreenActivity::class.java))
        }

        binding.rootViewLayout.viewTreeObserver.addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
            val r = Rect()
            binding.rootViewLayout.getWindowVisibleDisplayFrame(r)
            //  val heightDiff = view.rootView.height - (r.bottom - r.top)
            val heightDiff: Int =
                binding.rootViewLayout.rootView.height - binding.rootViewLayout.height
            if (heightDiff > 300) { // Value should be less than keyboard's height
                binding.onlineRecyclerView.visibility = View.GONE
            } else {
                binding.onlineRecyclerView.visibility = View.VISIBLE
            }
        })

//        binding.rootViewLayout.getViewTreeObserver().addOnGlobalLayoutListener(OnGlobalLayoutListener {
//            val r = Rect()
//            binding.rootViewLayout.getWindowVisibleDisplayFrame(r)
//            var s = r.bottom - r.top
//            var e =  view.rootView.height
//            val heightDiff =   (r.bottom - r.top) - view.rootView.height
//            if (s > 100) { // Value should be less than keyboard's height
//                Log.e("MyActivityk", "keyboard opened  ($s) - ($e) =  $heightDiff")
//            } else {
//                Log.e("MyActivityk", "keyboard closed ($s) - ($e) =  $heightDiff")
//            }
//        })

    }

    private fun openShareBottomSheet() {
        sheetDialog.show()
        shareIntent.closeSheet.setOnClickListener {
            sheetDialog.dismiss()
        }
        shareIntent.whatsappPicker.setOnClickListener {

            generateSharingLink(
                deepLink = "${PREFIX}/room/${currentNode}".toUri(),
            ) { generatedLink ->
                shareIntent.openWhatsapp(generatedLink)
            }
        }
        shareIntent.facebookPicker.setOnClickListener {
            generateSharingLink(
                deepLink = "${PREFIX}/room/${currentNode}".toUri(),
            ) { generatedLink ->
                shareIntent.openFacebook(generatedLink)
            }

        }
        shareIntent.instagramPicker.setOnClickListener {
            generateSharingLink(
                deepLink = "${PREFIX}/room/${currentNode}".toUri(),
            ) { generatedLink ->
                shareIntent.openInstagram(generatedLink)
            }

        }
        shareIntent.morePicker.setOnClickListener {
            generateSharingLink(
                deepLink = "${PREFIX}/room/${currentNode}".toUri(),
            ) { generatedLink ->
                shareIntent.openMore(generatedLink)
            }

        }
    }

    fun generateSharingLink(
        deepLink: Uri,
        getShareableLink: (String) -> Unit = {},
    ) {
        FirebaseDynamicLinks.getInstance().createDynamicLink().run {
            link = deepLink
            domainUriPrefix = PREFIX
            val vId = converter.extractYTId(url)
            val url = "https://www.youtube.com/oembed?url=youtube.com/watch?v=$vId&format=json"
            val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->

                val videoThumbnail = response.getString(THUMBNAIL_URl)
                try {
                    val url1 = URL(videoThumbnail)
                    val connection: HttpURLConnection = url1.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val input: InputStream = connection.inputStream
                    val myBitmap = BitmapFactory.decodeStream(input)
                    val out = FileOutputStream(videoThumbnail)
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    val path =
                        MediaStore.Images.Media.insertImage(
                            contentResolver,
                            myBitmap,
                            "Title",
                            null
                        )
                    val imagePreview = Uri.parse(path)
                    Log.d("sdcjcjscn", imagePreview.toString())
                    //            // Pass your preview Image Link here;
                    setSocialMetaTagParameters(
                        DynamicLink.SocialMetaTagParameters.Builder()
                            .setImageUrl(imagePreview)
                            .build()
                    )

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, { error ->
                error.printStackTrace()

            })
            val requestQueue1 = Volley.newRequestQueue(this@RoomActivity)
            requestQueue1.add(request)

            // Required
            androidParameters {
                build()
            }

            // Finally
            buildShortDynamicLink()
        }.also {
            it.addOnSuccessListener { dynamicLink ->
                getShareableLink.invoke(dynamicLink.shortLink.toString())
            }
            it.addOnFailureListener {

            }
        }
    }

    private fun loadOnlineUsers() {
        //  if (!isMaxData) {
        val query: Query =
            firebaseDatabaseService.ref.child(firebaseDatabaseService.CURRENT_ROOM)
                .child(currentNode)
                .child(firebaseDatabaseService.ONLINE_VIEWERS)

        query.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.hasChildren()) {
                    val list = ArrayList<OnlineModel>()
                    for (s in snapshot.children) {
                        list.add(OnlineModel(id = s.value.toString()))

                    }
                    Log.d("gfhdjk", "list -> ${list.size}")
                    onlineAdapter.updateLiveRoom(list, this@RoomActivity)
//                        val lastKeyQuery: Query =
//                            firebaseDatabaseService.ref.child(firebaseDatabaseService.CURRENT_ROOM)
//                                .child(currentNode)
//                                .child(firebaseDatabaseService.ONLINE_VIEWERS).orderByKey()
//                                .limitToLast(1)
//                        lastKeyQuery.addListenerForSingleValueEvent(object : ValueEventListener {
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                for (s in snapshot.children) {
//                                    lastKey = s.key
//                                }
//                                lastNode = list[list.size - 1].id
//                                if (!lastNode.equals(lastKey)) {
//                                    list.removeAt(list.size - 1)
//                                } else {
//                                    lastNode = "end"
//                                }
//
//                                isLoading = false
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {
//                                TODO("Not yet implemented")
//                            }
//                        })

                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //  TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                //  TODO("Not yet implemented")
                val model = snapshot.getValue(OnlineModel::class.java)
                Log.d("gfhdjk", "list DEL -> ${model!!.id}")
//                    list.remove(model)
                onlineAdapter.deleteOnlineUser(model)

                // list.remove(OnlineModel(id = snapshot.children.value.toString()))
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //  TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO("Not yet implemented")
            }

        })
        //  }


//        firebaseDao.getAllOnlineViewersOfVideo(key, currentNode)
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val list = ArrayList<OnlineModel>()
//                    for (s in snapshot.children) {
//                        val data: OnlineModel = s.getValue(OnlineModel::class.java)!!
//                        list.add(data)
//                        key = s.key
//                    }
//                    onlineAdapter.updateLiveRoom(list, this@RoomActivity)
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//
//            })
    }

    private fun setBlur(blurView: BlurView?) {
        val radius = 15f
        val decorView = window.decorView
        val rootView = decorView.findViewById<View>(android.R.id.content) as ViewGroup
        val windowBackground = decorView.background
        blurView!!.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            .setBlurAutoUpdate(true)
            .setHasFixedTransformationMatrix(true)
    }

    suspend fun updateViews() {
        if (typeOfRoom.equals("Private")) {
            val await = firebaseDatabaseService.ref.child("PrivateRooms")
                .child(firebaseDatabaseService.firebaseAuth.currentUser!!.uid).child("AllRooms")
                .child(currentNode)
                .get().await()
            val views = await.child(firebaseDatabaseService.VIEWS).value.toString()
            val v = views.toInt() + 1
            firebaseDatabaseService.ref.child("PrivateRooms")
                .child(firebaseDatabaseService.firebaseAuth.currentUser!!.uid).child("AllRooms")
                .child(currentNode).child(firebaseDatabaseService.VIEWS).setValue(v.toString())
                .await()
        } else {
            val await = firebaseDatabaseService.ref.child("PublicRooms")
                .child(currentNode)
                .get().await()
            val views = await.child(firebaseDatabaseService.VIEWS).value.toString()
            val v = views.toInt() + 1
            firebaseDatabaseService.ref.child("PublicRooms")
                .child(currentNode).child(firebaseDatabaseService.VIEWS).setValue(v.toString())
                .await()
        }


        firebaseDatabaseService.ref.child(firebaseDatabaseService.CURRENT_ROOM)
            .child(currentNode).child(firebaseDatabaseService.ONLINE_VIEWERS)
            .child(firebaseAuth.currentUser!!.uid).child("id")
            .setValue(firebaseAuth.currentUser!!.uid).await()
        sendMessageToRoomNode("", currentNode, "joined")
//        if (MainScreenActivity.listOfMember.isNotEmpty()) {
//            for (member in MainScreenActivity.listOfMember) {
//                sendPushNotification.sendPushNotificationToUser(
//                    member.id.toString(),
//                    firebaseDatabaseService.firebaseAuth.currentUser!!.uid,
//                    currentNode, liveModel, this
//                )
//            }
//        }
    }

    private fun fetchChatOfUsers() {
        binding.messageSendBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                if (binding.messageTypeEditText.text.toString().trim().isNotBlank()) {
                    sendMessageToRoomNode(
                        binding.messageTypeEditText.text.toString(),
                        currentNode,
                        "message"
                    )
                }
            }
        }
        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.stackFromEnd = true
        binding.messageRv.layoutManager = mLayoutManager
        binding.messageRv.adapter = messageAdpter
        CoroutineScope(Dispatchers.IO).launch {
            getAllMessagesFromRoomNode(currentNode)
        }
    }

    suspend fun sendMessageToRoomNode(message: String, currentNode: String, type: String) {
        binding.messageTypeEditText.text.clear()

        val cTime = CoroutineScope(Dispatchers.IO).async {
            NTPUDPClient().getTime(InetAddress.getByName("time-a.nist.gov")).message.receiveTimeStamp.time
        }

        val data = MessageData(
            message,
            cTime.await().toString(),
            firebaseAuth.currentUser!!.uid,
            firebaseAuth.currentUser!!.displayName.toString(),
            firebaseAuth.currentUser!!.photoUrl.toString(),
            type
        )

        firebaseDatabaseService.ref.child(firebaseDatabaseService.CURRENT_ROOM)
            .child(currentNode)
            .child(firebaseDatabaseService.MESSAGES).child(cTime.await().toString()).setValue(data)

    }

    private fun getAllMessagesFromRoomNode(roomid: String) {
        firebaseService.getAllMessage(roomid, messageAdpter)
        val query = firebaseDatabaseService.ref.child(firebaseDatabaseService.CURRENT_ROOM)
            .child(currentNode)
            .child(firebaseDatabaseService.MESSAGES)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    messagList.clear()
                    for (s in snapshot.children) {
                        val userData = s.getValue(MessageData::class.java)
                        messagList.add(userData!!)
                    }
                    messageAdpter.setAllMessages(this@RoomActivity, messagList)
                    binding.messageRv.adapter = messageAdpter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setAndPlayVideo(ytPlayer: YouTubePlayerView?) {
        ytPlayer?.initialize(
            R.string.api_key.toString(),
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider?,
                    player: YouTubePlayer?,
                    p2: Boolean
                ) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val current_Time =
                            NTPUDPClient().getTime(InetAddress.getByName("time-a.nist.gov")).message.receiveTimeStamp.time
                        val t = current_Time - currentNode.toLong()
                        val time = t.toInt()
                        player?.loadVideo(videoId, time)
                    }
                    player?.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL)
                    player?.setPlayerStateChangeListener(object :
                        YouTubePlayer.PlayerStateChangeListener {
                        override fun onLoading() {}
                        override fun onLoaded(p0: String?) {}
                        override fun onAdStarted() {}
                        override fun onVideoStarted() {
                            CoroutineScope(Dispatchers.IO).launch {
                                isPlay = 1
                                if (intent.getStringExtra(IS_LIVE).equals("false")) {
                                    val category = intent.getStringExtra("category")
                                    val hour =
                                        converter.getHours(player.durationMillis.toLong())
                                    val min =
                                        converter.getMinutes(player.durationMillis.toLong())
                                    val sec =
                                        converter.getSeconds(player.durationMillis.toLong())
                                    val duration = if (hour == 0L) {
                                        "$min:$sec"
                                    } else {
                                        "$hour:$min:$sec"
                                    }
                                    uploadData(
                                        url,
                                        duration,
                                        player.durationMillis.toString(),
                                        list, category
                                    )
                                } else {
                                    updateViews()
                                }
                            }
                        }

                        override fun onVideoEnded() {
                            CoroutineScope(Dispatchers.IO).launch {
                                isPlay = 1
                                player.seekToMillis(0)
                                player.pause()
                                deleteCurrentNode(currentNode)
                                startActivity(
                                    Intent(
                                        this@RoomActivity,
                                        MainScreenActivity::class.java
                                    )
                                )
                                finish()
                            }
                        }

                        override fun onError(p0: YouTubePlayer.ErrorReason?) {
                            Log.d("errorForPlayer",p0.toString())
                        }
                    })
                    player?.setPlaybackEventListener(object : YouTubePlayer.PlaybackEventListener {
                        override fun onPlaying() {
                            CoroutineScope(Dispatchers.IO).launch {
                                isPlay++
                                if (isPlay > 2) {
                                    val current_Time1 =
                                        NTPUDPClient().getTime(InetAddress.getByName("time-a.nist.gov")).message.receiveTimeStamp.time
                                    val t1 = current_Time1 - currentNode.toLong()
                                    val time1 = t1.toInt()

                                    player.seekToMillis(time1)
                                    isPlay = 1
                                }
                            }
                        }

                        override fun onPaused() {}
                        override fun onStopped() {
                        }
                        override fun onBuffering(p0: Boolean) {
                            CoroutineScope(Dispatchers.IO).launch {
                                if (p0) {
                                    val current_Time1 =
                                        NTPUDPClient().getTime(InetAddress.getByName("time-a.nist.gov")).message.receiveTimeStamp.time
                                    val t1 = current_Time1 - currentNode.toLong()
                                    val time1 = t1.toInt()
                                    player.seekToMillis(time1)
                                }
                            }
                        }

                        override fun onSeekTo(p0: Int) {}
                    })
                }

                override fun onInitializationFailure(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubeInitializationResult?
                ) {
                }
            })
    }

    private fun deleteCurrentNode(currentNode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            firebaseDatabaseService.ref.child(firebaseDatabaseService.CURRENT_ROOM)
                .child(currentNode).removeValue()
        }
    }

    private suspend fun updateViews1() {
        val await = firebaseDatabaseService.ref.child(firebaseDatabaseService.CURRENT_ROOM)
            .child(currentNode)
            .get().await()
        if (await.exists()) {
            val views = await.child(firebaseDatabaseService.VIEWS).value.toString()
            val v = views.toInt() + 1
            firebaseDatabaseService.ref.child(firebaseDatabaseService.CURRENT_ROOM)
                .child(currentNode).child(firebaseDatabaseService.VIEWS).setValue(v.toString())
                .await()
        }
        firebaseDatabaseService.ref.child(firebaseDatabaseService.CURRENT_ROOM)
            .child(currentNode).child(firebaseDatabaseService.ONLINE_VIEWERS)
            .child(firebaseAuth.currentUser!!.uid).child("id")
            .setValue(firebaseAuth.currentUser!!.uid).await()
        sendMessageToRoomNode("", currentNode, "joined")
    }

    fun uploadData(
        videoId: String,
        duration: String,
        videoDurationInMilli: String,
        list: ArrayList<OnlineModel>,
        category: String?
    ) {
        val vId = converter.extractYTId(videoId)
        val url = "https://www.youtube.com/oembed?url=youtube.com/watch?v=$vId&format=json"
        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            try {
                videoTitle = response.getString(TITLE)
                actionBar?.title = videoTitle
                actionBar?.subtitle = duration
                val rawUrl = response.getString(THUMBNAIL_URl);
                val videoThumbnail = rawUrl.replace("hqdefault", "mqdefault")
                val liveModel = LiveModel(
                    videoTitle,
                    "0",
                    duration,
                    videoId,
                    videoDurationInMilli,
                    firebaseAuth.currentUser!!.uid,
                    currentTime,
                    videoThumbnail,
                    typeOfRoom,
                    category,
                    list
                )
                CoroutineScope(Dispatchers.IO).launch {
                    if (typeOfRoom.equals("Private")) {
                        firebaseDatabaseService.ref.child(firebaseDatabaseService.PRIVATE_ROOM)
                            .child(firebaseDatabaseService.firebaseAuth.currentUser!!.uid)
                            .child(firebaseDatabaseService.ALL_ROOMS).child(currentTime)
                            .setValue(liveModel).await()
//                        for (member in list) {
//                            firebaseDatabaseService.ref.child(firebaseDatabaseService.PRIVATE_ROOM)
//                                .child(member.id.toString())
//                                .child(firebaseDatabaseService.ALL_ROOMS).child(currentTime)
//                                .setValue(liveModel).await()
//                        }
                    } else {
                        firebaseDatabaseService.ref.child(firebaseDatabaseService.PUBLIC_ROOMS)
                            .child(currentTime)
                            .setValue(liveModel).await()
                    }
                    firebaseDatabaseService.ref.child(firebaseDatabaseService.CURRENT_ROOM)
                        .child(currentNode)
                        .setValue(liveModel).await()
                    updateViews()
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, { error ->
            error.printStackTrace()
        })
        requestQueue.add(request)

    }

    override fun onPause() {
        super.onPause()
        CoroutineScope(Dispatchers.IO).launch {
            val await = firebaseDatabaseService.ref.child(firebaseDatabaseService.CURRENT_ROOM)
                .child(currentNode)
                .child(firebaseDatabaseService.ONLINE_VIEWERS)
                .child(firebaseAuth.currentUser!!.uid).get().await()
            if (await.exists()) {
                await.ref.removeValue()
                sendMessageToRoomNode("", currentNode, "left")
            }
        }
    }

    fun checkInternet() {
        var dilogBox = CustomProgressDialog()
        dilogBox.show(this,"Loading...")
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            // network is available for use
            override fun onAvailable(network: Network) {
                super.onAvailable(network)

                if (dilogBox.dialog.isShowing) {
                    dilogBox.dialog.dismiss()

                }
                Log.d("intcheck", "internet  all onavai is : ${network}")
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
                dilogBox.show(this@RoomActivity, "Internet connection lost!")
                Log.d("intcheck", "internet  all onlost is : ${network}")
            }
        }

        val connectivityManager =
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
        Log.d("intcheck", "internet  all time is : ${connectivityManager.activeNetwork}")

    }


    companion object {
        const val ITEM_COUNT = 10
        const val MESSAGE_COUNT = 20
        const val URl = "url"
        const val IS_LIVE = "isLive"
        const val TRUE = "true"
        const val TITLE = "title"
        const val VIDEO_TIME = "video_time"
        var ADMIN_ID = "adminId"
        const val NODE_ID = "nodeId"
        const val VIDEO = "video"
        const val THUMBNAIL_URl = "thumbnail_url"
        const val PREFIX = "https://videostreamingapp.page.link"
        lateinit var adminId: String
    }

}
