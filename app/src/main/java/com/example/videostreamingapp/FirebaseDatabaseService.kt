package com.example.videostreamingapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class firebaseDatabaseService {

    companion object {
        const val PUBLIC_ROOMS = "PublicRooms"
        const val PRIVATE_ROOM = "PrivateRooms"
        const val CURRENT_ROOM = "CurrentlyPlaying"
        const val ALL_ROOMS = "AllRooms"
        const val VIEWS = "views"
        const val ONLINE_VIEWERS = "OnlineViewers"
        const val MESSAGES = "Messages"
        const val CURRENT_PLAYER = "CurrentPlayer"
        const val ID = "id"
        const val FRIEND = "Friend"
        const val RECEIVE = "Receive"
        const val SENT = "sent"
        const val SENT1 = "Sent"
        const val UID = "uid"
        const val RECEIVE1 = "receive"
        const val CONNECTED = "Connected"
        const val CONNECTED1 = "connected"
        const val UNCONNECTED = "unconnected"
        const val EMAIL = "email"
        const val UNAME = "uname"
        const val NAME = "name"
        const val IMAGE_URI = "imageUri"
        const val FOLLOWER = "follower"
        const val REQUEST = "request"
        const val LATITUDE = "lati"
        const val LONGITUDE = "long"
        const val PRIVATE = "private"
        const val MUSIC_FILES = "MusicFiles"
        const val ALL_SONGS = "AllSongs"
        const val MUSIC_PLAYING = "MusicPlaying"
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        var database = Firebase.database
        val ref = database.getReference("LiveRoom")
        val musicRef = database.getReference("MusicData")
        val userRef = database.getReference("Users")
        val suggestionRef = database.getReference("Suggestions")
        val firebaseStorage: StorageReference = FirebaseStorage.getInstance().getReference()
    }

    init {
        database.setPersistenceEnabled(true)
        userRef.keepSynced(true)
    }
}