package com.example.videostreamingapp.bottomnavfragment.createroom

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.videostreamingapp.bottomnavfragment.createroom.audio.AudioUpload
import com.example.videostreamingapp.bottomnavfragment.createroom.video.VideoUpload

class ViewPagerCreateRoomAdapter(fm:FragmentManager):FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> {
                return AudioUpload()
            }
            1 -> {
                return VideoUpload()
            }else->{
                return VideoUpload()
            }
        }
    }
    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> {
                return "Audio"
            }
            1 -> {
                return "Video"
            }

        }
        return super.getPageTitle(position)
    }
}