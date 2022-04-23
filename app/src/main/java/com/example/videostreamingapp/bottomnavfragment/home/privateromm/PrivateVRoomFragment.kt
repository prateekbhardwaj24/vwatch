package com.example.videostreamingapp.bottomnavfragment.home.privateromm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videostreamingapp.bottomnavfragment.home.HomeViewModel
import com.example.videostreamingapp.bottomnavfragment.home.VideoAdapter
import com.example.videostreamingapp.databinding.FragmentPrivateVRoomBinding
import com.example.videostreamingapp.firebase.HomeFirebaseDao
import com.example.videostreamingapp.firebaseDatabaseService
import com.example.videostreamingapp.model.LiveModel
import com.example.videostreamingapp.ui.CustomProgressDialog
import com.firebase.ui.database.paging.DatabasePagingOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PrivateVRoomFragment : Fragment() {

    private lateinit var binding: FragmentPrivateVRoomBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var homeFirebaseDao: HomeFirebaseDao
    private lateinit var pagingAdapter: VideoAdapter
    private  var progressDialog: CustomProgressDialog = CustomProgressDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPrivateVRoomBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.liveRecycler.layoutManager = LinearLayoutManager(requireContext())
        homeFirebaseDao = HomeFirebaseDao()

      //  progressDialog.show(requireContext(),"Loading...")
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
        val query = firebaseDatabaseService.ref.child("PrivateRooms")
            .child(firebaseDatabaseService.firebaseAuth.currentUser!!.uid)
            .child("AllRooms")
        val config =
            PagingConfig( /* page size */2,  /* prefetchDistance */10,  /* enablePlaceHolders */
                false
            )

        val options = DatabasePagingOptions.Builder<LiveModel>()
            .setLifecycleOwner(this)
            .setQuery(query, config, LiveModel::class.java)
            .build()

        pagingAdapter = VideoAdapter(options, requireContext(),"Private")
        binding.liveRecycler.adapter = pagingAdapter
        progressDialog.dialog.dismiss()
    }

}