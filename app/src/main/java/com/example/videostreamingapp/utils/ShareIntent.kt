package com.example.videostreamingapp.utils

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.videostreamingapp.R
import com.example.videostreamingapp.databinding.ActivityRoomBinding
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeThumbnailLoader
import com.google.android.youtube.player.YouTubeThumbnailView

class ShareIntent(private val context: Context, view: View) {

    val closeSheet = view.findViewById<ImageView>(R.id.close_sheet)
    val whatsappPicker = view.findViewById<LinearLayout>(R.id.whatsapp_picker)
    val facebookPicker = view.findViewById<LinearLayout>(R.id.facebook_picker)
    val instagramPicker = view.findViewById<LinearLayout>(R.id.instagram_picker)
    val morePicker = view.findViewById<LinearLayout>(R.id.more_picker)

    fun openWhatsapp(generatedLink: String) {
        val message = generatedLink
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, message)
        share.setPackage("com.whatsapp")
        context.startActivity(share)
    }

    fun openFacebook(generatedLink: String) {
        val message = generatedLink
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, message)
        share.setPackage("com.facebook.katana")
        context.startActivity(share)
    }

    fun openInstagram(generatedLink: String) {
        val message = generatedLink
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, message)
        share.setPackage("com.instagram.android")
        context.startActivity(share)
    }

    fun openMore(generatedLink: String) {
        val message = generatedLink
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, message)
        context.startActivity(share)
    }

    fun setThumbnail(videoId: String, binding: ActivityRoomBinding) {
//        CoroutineScope(Dispatchers.IO).launch {
        val onThumbnailLoadedListener: YouTubeThumbnailLoader.OnThumbnailLoadedListener =
            object : YouTubeThumbnailLoader.OnThumbnailLoadedListener {
                override fun onThumbnailLoaded(p0: YouTubeThumbnailView?, p1: String?) {}
                override fun onThumbnailError(
                    p0: YouTubeThumbnailView?,
                    p1: YouTubeThumbnailLoader.ErrorReason?
                ) {
                }
            }
        binding.ytThumbnail.initialize(
            R.string.api_key.toString(),
            object : YouTubeThumbnailView.OnInitializedListener {
                override fun onInitializationSuccess(
                    p0: YouTubeThumbnailView?,
                    p1: YouTubeThumbnailLoader?
                ) {
                    p1?.setVideo(videoId)
                    p1?.setOnThumbnailLoadedListener(onThumbnailLoadedListener)
                }

                override fun onInitializationFailure(
                    p0: YouTubeThumbnailView?,
                    p1: YouTubeInitializationResult?
                ) {
                }
            })
//        }

    }
}