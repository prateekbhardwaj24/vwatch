package com.example.videostreamingapp.bottomnavfragment.home.publicroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videostreamingapp.bottomnavfragment.home.HomeViewModel
import com.example.videostreamingapp.bottomnavfragment.home.VideoAdapter
import com.example.videostreamingapp.databinding.FragmentPrivateVRoomBinding
import com.example.videostreamingapp.firebase.HomeFirebaseDao
import com.example.videostreamingapp.firebaseDatabaseService
import com.example.videostreamingapp.model.LiveModel
import com.example.videostreamingapp.ui.CustomProgressDialog
import com.firebase.ui.database.paging.DatabasePagingOptions
import kotlinx.coroutines.launch


class PublicVRoomFragment : Fragment() {

    private lateinit var binding: FragmentPrivateVRoomBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var homeFirebaseDao: HomeFirebaseDao
    private lateinit var pagingAdapter: VideoAdapter
    private var progressDialog: CustomProgressDialog = CustomProgressDialog()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrivateVRoomBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.liveRecycler.layoutManager = LinearLayoutManager(requireContext())
//        binding.liveRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        homeFirebaseDao = HomeFirebaseDao()
        showData()
        binding.refreshLayout.setOnRefreshListener {
            showData()
        }


        return binding.root
    }

    private fun showData() {
        viewLifecycleOwner.lifecycleScope.launch {
            progressDialog.show(requireContext(), "Loading...")
            getData()
        }
        if (binding.refreshLayout.isRefreshing){
            binding.refreshLayout.isRefreshing = false;
        }
    }

    private fun getData() {
        val query = firebaseDatabaseService.ref.child("PublicRooms")
        val config =
            PagingConfig( /* page size */15,  /* prefetchDistance */10,  /* enablePlaceHolders */
                true
            )

        val options = DatabasePagingOptions.Builder<LiveModel>()
            .setLifecycleOwner(this)
            .setQuery(query, config, LiveModel::class.java)
            .build()

        pagingAdapter = VideoAdapter(options, requireContext(), "Public")
        binding.liveRecycler.adapter = pagingAdapter
        progressDialog.dialog.dismiss()

    }


    override fun onStart() {
        super.onStart()
        pagingAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        pagingAdapter.stopListening()
    }
}