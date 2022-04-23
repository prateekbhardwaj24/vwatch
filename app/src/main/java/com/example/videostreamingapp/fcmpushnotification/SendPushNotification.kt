package com.example.videostreamingapp.fcmpushnotification

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.videostreamingapp.R
import com.example.videostreamingapp.firebaseDatabaseService
import com.example.videostreamingapp.model.LiveModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.json.JSONException
import org.json.JSONObject

class SendPushNotification {

    var userProfile: String? = null
    fun sendPushNotificationToUser(
        userId: String,
        currentUserId: String,
        roomId: String,
        model:LiveModel,
        context: Context
    ) {
        firebaseDatabaseService.userRef.child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val token = snapshot.child("token").value.toString()
                        // val name = snapshot.child("name").value.toString()
                        firebaseDatabaseService.userRef.child(currentUserId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val user_n = dataSnapshot.child("name").value.toString()
                                    userProfile =
                                        dataSnapshot.child("imageUri").value.toString()
                                    val message = "$user_n has invited you to join live room"
                                    val to = JSONObject()
                                    val data = JSONObject()
                                    try {
                                        data.put("notificationTitle", message)
                                        data.put("nodeId", roomId)
                                        data.put("type", "Video")
                                        data.put("title", model.title)
                                        data.put("adminId", model.adminId)
                                        data.put("userProfile", userProfile)
                                        data.put("url", model.videoId)
                                        data.put("isLive", "true")
                                        data.put("video_time", model.duration)
                                        to.put("to", token)
                                        to.put("data", data)


                                        sendNotification(to, context)
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {}
                            })
                    }

                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun sendNotification(to: JSONObject, context: Context) {
        val request = object : JsonObjectRequest(
            Request.Method.POST,
            "https://fcm.googleapis.com/fcm/send",
            to,
            Response.Listener { response ->
            },
            Response.ErrorListener {
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val header: MutableMap<String, String> = HashMap()
                val fcmServerKey = context.resources.getString(R.string.fcm_key)
                header["content-type"] = "application/json"
                header["authorization"] = "key=$fcmServerKey"
                return header
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
//
//        VolleySingleton.getInstance(context)
//            .addToRequestQueue(request)

        val queue = Volley.newRequestQueue(context)
        request.retryPolicy =
            DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        queue.add(request)

    }

   fun sendAudioNotification(
       currentUserId: String,
       userId: String,
       roomId: String,
       roomName: String,
       context: Context
   ){
       firebaseDatabaseService.userRef.child(userId)
           .addListenerForSingleValueEvent(object : ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {
                   if (snapshot.exists()) {
                       val token = snapshot.child("token").value.toString()
                       firebaseDatabaseService.userRef.child(currentUserId)
                           .addListenerForSingleValueEvent(object : ValueEventListener {
                               override fun onDataChange(dataSnapshot: DataSnapshot) {
                                   val user_n = dataSnapshot.child("name").value.toString()
                                   userProfile =
                                       dataSnapshot.child("imageUri").value.toString()
                                   val message = "$user_n has invited you to join Audio room"
                                   val to = JSONObject()
                                   val data = JSONObject()
                                   try {
                                       data.put("notificationTitle", message)
                                       data.put("userProfile", userProfile)
                                       data.put("type", "audio")
                                       data.put("roomName", roomName)
                                       data.put("roomIdandTime",roomId)
                                       to.put("to", token)
                                       to.put("data", data)

                                       sendNotification(to, context)
                                   } catch (e: JSONException) {
                                       e.printStackTrace()
                                   }
                               }

                               override fun onCancelled(error: DatabaseError) {}
                           })
                   }

               }

               override fun onCancelled(error: DatabaseError) {}
           })
   }

}