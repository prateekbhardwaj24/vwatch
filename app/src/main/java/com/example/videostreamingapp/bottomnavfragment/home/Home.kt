package com.example.videostreamingapp.bottomnavfragment.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.videostreamingapp.bottomnavfragment.home.publicroom.VideoPagerAdapter
import com.example.videostreamingapp.databinding.HomeFragmentBinding


class Home : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: HomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = HomeFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.slidingViewPager0.adapter = VideoPagerAdapter(childFragmentManager)
        binding.pagerTabsLayout0.setupWithViewPager(binding.slidingViewPager0)

        return binding.root
    }

}