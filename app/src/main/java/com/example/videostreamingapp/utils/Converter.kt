package com.example.videostreamingapp.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.net.ntp.NTPUDPClient
import java.net.InetAddress
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern


class Converter() {
    val time:MutableLiveData<Long> = MutableLiveData()
    val _time = time as LiveData<Long>
    fun extractYTId(ytUrl: String?): String? {
        var vId: String? = null
        val pattern: Pattern = Pattern.compile(
            "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
            Pattern.CASE_INSENSITIVE
        )
        val matcher: Matcher = pattern.matcher(ytUrl)
        if (matcher.matches()) {
            vId = matcher.group(1)
        }
        return vId
    }

    fun getHours(milliseconds: Long): Long {
        return (TimeUnit.MILLISECONDS.toHours(milliseconds)
                - TimeUnit.DAYS.toHours(
            TimeUnit.MILLISECONDS.toDays(
                milliseconds
            )
        ))
    }

    fun getMinutes(milliseconds: Long): Long {
        return (TimeUnit.MILLISECONDS.toMinutes(milliseconds)
                - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(
                milliseconds
            )
        ))
    }

    fun getSeconds(milliseconds: Long): Long {
        return (TimeUnit.MILLISECONDS.toSeconds(milliseconds)
                - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(
                milliseconds
            )
        ))
    }

    fun getLocationByLatLong(
        lat: String,
        longi: String,
        context: Context
    ): MutableLiveData<String> {
        var result: MutableLiveData<String> = MutableLiveData()
        var location: String = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address?>
        try {
            addresses = geocoder.getFromLocation(
                lat.toDouble(),
                longi.toDouble(), 1
            )
            if (addresses.isNotEmpty()) {
                val address: String = addresses[0]
                    ?.getAddressLine(0)!!
                val locality: String = addresses[0]!!.locality!!
                val subLocality: String = addresses[0]!!.subLocality!!
                val state: String = addresses[0]!!.adminArea
                val country: String = addresses[0]!!.countryName
                val postalCode: String = addresses[0]!!.postalCode
                val knownName: String = addresses[0]!!.featureName
                location = if (subLocality.isNotEmpty()) {
                    "$locality, $country"
                } else {
                    "$address, $country"
                }
                result.postValue(location)
            }
        } catch (e: Exception) {
        }

        return result
    }

    fun getCurrentTime1() {

//        val TIME_SERVER =
//            val timeClient =
//            val inetAddress: InetAddress =
//            val timeInfo =
//            val date =
//            val system = System.currentTimeMillis()
//            Log.d("vhgfhfhfhg", "$date, Current : $system")

       CoroutineScope(Dispatchers.IO).launch {
          time.postValue(NTPUDPClient().getTime(InetAddress.getByName("time-a.nist.gov")).message.receiveTimeStamp.time)
       }
    }
    fun getCurrentTime():Long {

//        val TIME_SERVER =
//
//            val timeClient =
//            val inetAddress: InetAddress =
//            val timeInfo =
//            val date =
//            val system = System.currentTimeMillis()
//            Log.d("vhgfhfhfhg", "$date, Current : $system")


           return NTPUDPClient().getTime(InetAddress.getByName("time-a.nist.gov")).message.receiveTimeStamp.time

    }

}