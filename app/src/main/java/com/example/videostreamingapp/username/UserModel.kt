package com.example.videostreamingapp.username

data class UserModel(
    val uid: String = "",
    val name: String? = "",
    val email: String? = "",
    val uname: String? = "",
    val imageUri: String = "",
    val gender: String = "",
    val lati: String? = null,
    val long: String? = null,
    val token: String = "",
)

data class UserData(var name: String?=null, var imageUri: String?=null)
//data class UserData(var name: String?=null, var imageUri: String?=null)

