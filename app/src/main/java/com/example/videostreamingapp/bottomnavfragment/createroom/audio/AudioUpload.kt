package com.example.videostreamingapp.bottomnavfragment.createroom.audio

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import android.content.Context
import android.os.Build
import com.example.videostreamingapp.R

import android.widget.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.Modals.AudioModal


class AudioUpload : Fragment() {

    lateinit var chooseBtn:Button
    lateinit var musicImage:ImageView
    lateinit var musicName:TextView
    lateinit var musicSize:TextView
    lateinit var musicLength:TextView

    lateinit var allSongsRv: RecyclerView
    lateinit var audioAdapter: AudioAdapter
    lateinit var songSearch: SearchView
    companion object {
        var songList: ArrayList<AudioModal> = ArrayList()
    }

    private lateinit var viewModel: AudioUploadViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root:View = inflater.inflate(R.layout.audio_upload_fragment, container, false)

        viewModel = ViewModelProvider(this).get(AudioUploadViewModel::class.java)


        //load all songs
        allSongsRv.layoutManager = LinearLayoutManager(requireContext())
        allSongsRv.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        audioAdapter = AudioAdapter()



        return root
    }

    @SuppressLint("Range")
    private fun getAllAudioFromDevice(requireContext: Context) {

//        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//        val projection = arrayOf(
//            MediaStore.Audio.AudioColumns.DATA,
//            MediaStore.Audio.AudioColumns.TITLE,
//            MediaStore.Audio.AudioColumns.ALBUM,
//            MediaStore.Audio.AudioColumns.DISPLAY_NAME,
//
//
//            //Albums
//            MediaStore.Audio.AlbumColumns.ALBUM,
//           MediaStore.Audio.AlbumColumns.ALBUM_ID,
//
//            )
//        val c: Cursor? = requireContext.contentResolver.query(
//            uri,
//            projection,
//            null,
//            null,
//            MediaStore.Audio.Media.DATE_ADDED  + " DESC"
//        )
//        if (c != null) {
//            Toast.makeText(requireContext,"insert is ${songList.size}",Toast.LENGTH_SHORT).show()
//
//            while (c.moveToNext()) {
//              //  Toast.makeText(requireContext,"length is1 ${songList.size}",Toast.LENGTH_SHORT).show()
//
//                val audioModel: AudioModal
//                val path: String = c.getString(0)
//                val name: String = c.getString(1)
//                val album: String = c.getString(2)
//                val displayName: String = c.getString(3)
////                val artist: String = c.getString(4)
//                val Albumpath: String = c.getString(4)
////                val NameArtitstAlbum: String = c.getString(6)
//               val AlbumId: String = c.getString(5)
////                val dateAdded: String = c.getString(9)
//
//                val albumIdc =
//                   c.getLong(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
//
//                val uri = Uri.parse("content://media/external/audio/albumart")
//               val artUri = Uri.withAppendedPath(uri, albumIdc.toString())
//
//                audioModel =
//                    AudioModal(
//                        aName = name,
//                        aAlbum = album,
//                        aArtist = "artist",
//                        aPath = path,
//                        displName = displayName,
//                        albumPath = Albumpath,
//                        albumId = AlbumId,
//                        nameArtistAlbum = "NameArtitstAlbum",
//                        audioIcon = artUri.toString(),
//                        dateAdded = "dateAdded",
//
//                        )
//                Log.d("Name :$name -> ", " Album :$album")
//                Log.d("Path :$path -> ", " Artist :")
//
//                songList.add(audioModel)
//               // Toast.makeText(requireContext,"length is ${songList.size}",Toast.LENGTH_SHORT).show()
//
//            }
//
//            c.close()
//        }

    }

//
//    private fun openIntent() {
//        val intent: Intent
//        intent = Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER)
//        intent.type = "audio/mpeg"
//
//        if (context?.let { intent.resolveActivity(it.packageManager) } != null) {
//            startActivityForResult(intent, 1)
//        }
//
//    }
//    @SuppressLint("Range")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
//            if (data != null && data.data != null) {
//                val audioFileUri = data.data
//                val file = File(audioFileUri!!.path)
//
//                // Now you can use that Uri to get the file path, or upload it, ... show
//
//                var fileName = ""
//                var cursor: Cursor? = null
//                cursor = requireContext().contentResolver.query(
//                    audioFileUri, arrayOf( MediaStore.Audio.AlbumColumns.ALBUM_ID), null, null, null
//                )
//
//                if (cursor != null) {
//                   cursor.moveToFirst()
//                    val albumIdc =
//                        cursor?.getLong(0)
//                    cursor!!.close()
//                    val uri = Uri.parse("content://media/external/audio/albumart")
//                    val artUri = Uri.withAppendedPath(uri, albumIdc.toString())
//                    Glide.with(requireContext()).load(artUri).into(musicImage)
//
//                }
//
//
//            }
//        }
//    }
//

}


