package com.example.videostreamingapp.bottomnavfragment.profile

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.videostreamingapp.R
import com.example.videostreamingapp.databinding.ProfileFragmentBinding
import com.example.videostreamingapp.firebaseDatabaseService
import com.example.videostreamingapp.loginwithgmail.LoginWithGmail
import com.example.videostreamingapp.ui.SuggestionFormActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class Profile : Fragment() {

    private lateinit var binding: ProfileFragmentBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProfileFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        val appActivity = activity as AppCompatActivity?
        appActivity?.setSupportActionBar(binding.toolbar)
        appActivity?.title = resources.getString(R.string.profile)


        binding.facebook.setOnClickListener {
            val facebookId = "fb://page/115611817790320"
            val urlPage = "http://www.facebook.com/Vwatch-party-115611817790320"

            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(facebookId)))
            } catch (e: java.lang.Exception) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlPage)))
            }

        }
        binding.instagram.setOnClickListener {
            val uri = Uri.parse("https://www.instagram.com/vwatch_party/")
            val likeIng = Intent(Intent.ACTION_VIEW, uri)
            likeIng.setPackage("com.instagram.android")
            try {
                startActivity(likeIng)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.instagram.com/vwatch_party/")
                    )
                )
            }
        }
        binding.linkedin.setOnClickListener {

        }
        binding.twitter.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("twitter://user?screen_name=VwatchP")
                    )
                )
            } catch (e: Exception) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://twitter.com/#!/VwatchP")
                    )
                )
            }
        }
        binding.contactUs.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:vwatchparty@gmail.com")
            }
            startActivity(Intent.createChooser(emailIntent, "Contact help"))
        }
        binding.tutorial.setOnClickListener {

        }
        binding.suggestions.setOnClickListener {
            startActivity(Intent(requireContext(), SuggestionFormActivity::class.java))
        }
        binding.logout.setOnClickListener {
            firebaseDatabaseService.firebaseAuth.currentUser?.getIdToken(false)
                ?.addOnSuccessListener { result ->
                    when (result.signInProvider) {
                        "google.com" -> {
                            GoogleSignIn.getClient(
                                requireContext(),
                                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .build()
                            ).signOut()
                            startActivity(Intent(requireContext(), LoginWithGmail::class.java))
                            requireActivity().finish()
                        }
                    }
                }
        }

        setUpProfileData()

        return binding.root
    }

    private fun setUpProfileData() {
        viewModel.fetchCurrentProfileData().observe(requireActivity(), Observer {
            it?.let {
                binding.userName.text = it.name
                binding.userUName.setText(it.uname)
                binding.email.text = it.email
                Glide.with(requireActivity()).load(it.imageUri).into(binding.userProfile)
            }
        })
    }

}