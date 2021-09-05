package com.legendbois.memeindexer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.util.*
import kotlin.math.abs
import kotlin.math.max

object MiscHelper {
    private val EMAIL_IDS = arrayOf("jatinsaini580@gmail.com")

    fun sendMail(context: Activity, emailSubject: String, emailText: String? = null){
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data= Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, EMAIL_IDS)
        intent.putExtra(Intent.EXTRA_SUBJECT,emailSubject)

        intent.putExtra(Intent.EXTRA_TEXT, emailText)
        try{
            context.startActivity(Intent.createChooser(intent,"Send Mail..."))
        }
        catch ( ex : android.content.ActivityNotFoundException){
            Toast.makeText(context.applicationContext,"There are no email clients installed", Toast.LENGTH_SHORT).show()
        }
    }


    fun getStartDelay(hour: Int, minutes: Int): Long{
        val targetTime = Calendar.getInstance()
        targetTime.set(Calendar.HOUR_OF_DAY, hour)
        targetTime.set(Calendar.MINUTE, minutes)
        targetTime.set(Calendar.SECOND, 0)
        targetTime.set(Calendar.MILLISECOND, 0)


        val currentTime = Calendar.getInstance()
        if(abs(targetTime.timeInMillis - currentTime.timeInMillis) > 300000){
            if (targetTime.timeInMillis < currentTime.timeInMillis){
                targetTime.add(Calendar.DAY_OF_MONTH, 1)
            }
            return targetTime.timeInMillis/1000 - currentTime.timeInMillis/1000
        }
        return 0
    }

    fun getPastTimeFromKey(timeRangeKey: String): Long{
        val clearAllAfterTime = ConstantsHelper.TIME_RANGES[timeRangeKey] ?: 0
        return max(0, System.currentTimeMillis() - clearAllAfterTime)
    }
}