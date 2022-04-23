package com.example.videostreamingapp.bottomnavfragment.home.publicroom

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.videostreamingapp.bottomnavfragment.home.privateromm.PrivateVRoomFragment

class VideoPagerAdapter(fm : FragmentManager): FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                return PublicVRoomFragment()
            }
            1 -> {
                return PrivateVRoomFragment()
            }
            else -> {
                return PublicVRoomFragment()
            }
        }

    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> {
                return "Public Rooms"
            }
            1->{
                return "Private Rooms"
            }

        }
        return super.getPageTitle(position)
    }
}