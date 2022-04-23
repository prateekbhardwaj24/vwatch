package com.example.videostreamingapp.model

data class LiveModel(
    var title:String?=null,
    var views:String?=null,
    var duration:String? = null,
    var videoId:String? = null,
    var videoUploadTime:String? = null,
    var adminId:String? = null,
    var currentDuration:String? = null,
    var video_thumbnail:String? = null,
    var type:String? = null,
    var category:String?=null,
var members:ArrayList<OnlineModel> = ArrayList()
)

data class OnlineModel(var id:String="")
