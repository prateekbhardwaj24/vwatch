package com.example.videostreamingapp.fcmpushnotification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.videostreamingapp.R
import com.example.videostreamingapp.ui.RoomActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class FcmService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            val map: Map<String, String> = remoteMessage.data
            val title = map["notificationTitle"]
            val sender = map["adminId"]
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                createOreoNotification(title, sender, remoteMessage)
            } else {
                createNormalNotification(title, sender, remoteMessage)
            }
        }
    }

    private fun createNormalNotification(
        title: String?,
        sender: String?,
        remoteMessage: RemoteMessage
    ) {
        val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "1000")

        if (remoteMessage.data["type"].equals("video")){
            builder.setContentTitle(title)
                .setSmallIcon(R.drawable.app_logo)
                .setAutoCancel(true)
                .setSound(uri)
            val intent = Intent(this, RoomActivity::class.java)
            intent.putExtra("url", remoteMessage.data["url"])
            intent.putExtra("isLive", "true")
            intent.putExtra("nodeId", remoteMessage.data["nodeId"])
            intent.putExtra("adminId", remoteMessage.data["adminId"])
            intent.putExtra("title", remoteMessage.data["title"])
            intent.putExtra("video_time", remoteMessage.data["video_time"])
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            builder.setContentIntent(pendingIntent)
            if (remoteMessage.data["userProfile"] != null) {
                val imageBitmap: Bitmap = getImageBitmap(remoteMessage.data["userProfile"])!!
                builder.setLargeIcon(imageBitmap)
            }
        }

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random().nextInt(85 - 65), builder.build())
    }

    private fun createOreoNotification(
        title: String?,
        sender: String?,
        remoteMessage: RemoteMessage
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("1000", "postLike", NotificationManager.IMPORTANCE_HIGH)
            channel.setShowBadge(true)
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.description = "Room Description"
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
            val notification: Notification.Builder = Notification.Builder(this, "1000")

            if (remoteMessage.data["type"].equals("video")){
                notification.setContentTitle(title)
                    .setSmallIcon(R.drawable.app_logo)
                    .setAutoCancel(true)

                val intent = Intent(this, RoomActivity::class.java)
                intent.putExtra("url", remoteMessage.data["url"])
                intent.putExtra("isLive", "true")
                intent.putExtra("nodeId", remoteMessage.data["nodeId"])
                intent.putExtra("adminId", remoteMessage.data["adminId"])
                intent.putExtra("title", remoteMessage.data["title"])
                intent.putExtra("video_time", remoteMessage.data["video_time"])
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                val pendingIntent =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
                notification.setContentIntent(pendingIntent)
                if (remoteMessage.data["userProfile"] != null) {
                    val imageBitmap = getImageBitmap(remoteMessage.data["userProfile"])
                    notification.setLargeIcon(imageBitmap)
                }
            }

            manager.notify(Random().nextInt(85 - 65), notification.build())

        }
    }

    private fun getImageBitmap(userProfile: String?): Bitmap? {
        return try {
            val url = URL(userProfile)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            val myBitmap = BitmapFactory.decodeStream(input)
            val output = Bitmap.createBitmap(
                myBitmap.width,
                myBitmap.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val color: Int = Color.RED
            val paint = Paint()
            val rect = Rect(0, 0, myBitmap.width, myBitmap.height)
            val rectF = RectF(rect)
            paint.setAntiAlias(true)
            canvas.drawARGB(0, 0, 0, 0)
            paint.setColor(color)
            canvas.drawOval(rectF, paint)
            paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
            canvas.drawBitmap(myBitmap, rect, rect, paint)
            myBitmap.recycle()
            output
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}


// data.put("type", notificationType)

