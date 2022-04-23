package com.example.videostreamingapp.bottomnavfragment.createroom.video

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.videostreamingapp.R
import com.example.videostreamingapp.bottomnavfragment.createroom.CreateRoomViewModel
import com.example.videostreamingapp.mainscreen.MainScreenActivity
import com.example.videostreamingapp.ui.RoomActivity

class VideoUpload : Fragment() {


    private lateinit var viewModel: VideoUploadViewModel
    private lateinit var videoUrl: EditText
    private lateinit var url:String
    private lateinit var button: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(VideoUploadViewModel::class.java)

        val root:View = inflater.inflate(R.layout.video_upload_fragment,container,false)

        videoUrl = root.findViewById(R.id.videoUrl)
        button = root.findViewById(R.id.button)

        videoUrl.setText(MainScreenActivity.newUrl)


        button.setOnClickListener(View.OnClickListener {
            url = videoUrl.text.toString()
            val intent = Intent(requireActivity(), RoomActivity::class.java)
            intent.putExtra("url", url)
            intent.putExtra("isLive", "false")
            startActivity(intent)
        })
        return root
    }



}