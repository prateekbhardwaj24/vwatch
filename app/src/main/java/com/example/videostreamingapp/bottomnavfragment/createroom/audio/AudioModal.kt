package com.example.musicplayer.Modals

import android.net.Uri


data class AudioModal(
    var aPath: String,
    var aName: String,
    var aAlbum: String,
    var displName: String,


    var aArtist: String,


    var albumPath: String, //this is equal to aAlbum = album name
    var audioIcon: String,
    var albumId: String,
    val dateAdded: String,
    val musicUri: Uri,
    val duration: String,
   // val musicIcon: Bitmap,


    )
