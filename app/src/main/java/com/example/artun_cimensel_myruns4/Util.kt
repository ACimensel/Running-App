package com.example.artun_cimensel_myruns4

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

object Util {
    fun checkPermissions(activity: Activity?) {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
    }

    fun getBitmap(context: Context, imgUri: Uri): Bitmap {
        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imgUri))
        val matrix = Matrix()
        matrix.setRotate(90f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun kmToMiles(km: Double): Double {
        return km * 0.62137
    }

    fun milesToKm(miles: Double): Double {
        return miles * 1.60934
    }

    fun speedToCorrectUnitStr(_speed: Double, isMetric: Boolean = true): String {
        var speed = _speed
        if(!isMetric) speed = kmToMiles(_speed)

        var integerPart = speed.toInt()
        var decimalPartStr = '.' + speed.toString().split(".")[1]

        var retStr = integerPart.toString()
        if(decimalPartStr != ".0"){
            retStr += decimalPartStr

            val dbl = BigDecimal(retStr.toDouble()).setScale(2, RoundingMode.HALF_EVEN )
            integerPart = dbl.toInt()
            decimalPartStr = dbl.toString().split(".")[1]

            var newDecimalPartStr = ""
            if(decimalPartStr[1] != '0') newDecimalPartStr = "${decimalPartStr[1]}"
            if(decimalPartStr[0] != '0' || decimalPartStr[1] != '0') newDecimalPartStr = "${decimalPartStr[0]}$newDecimalPartStr"
            if(newDecimalPartStr != "") newDecimalPartStr = ".$newDecimalPartStr"

            retStr = integerPart.toString() + newDecimalPartStr
        }

        retStr += if(!isMetric) " miles/h"
        else " km/h"

        return retStr
    }

    fun distanceToCorrectUnitStr(_distance: Double, isMetric: Boolean = true): String {
        var distance = _distance
        if(!isMetric) distance = kmToMiles(_distance)

        var integerPart = distance.toInt()
        var decimalPartStr = '.' + distance.toString().split(".")[1]

        var retStr = integerPart.toString()
        if(decimalPartStr != ".0"){
            retStr += decimalPartStr

            val dbl = BigDecimal(retStr.toDouble()).setScale(2, RoundingMode.HALF_EVEN )
            integerPart = dbl.toInt()
            decimalPartStr = dbl.toString().split(".")[1]

            var newDecimalPartStr = ""
            if(decimalPartStr[1] != '0') newDecimalPartStr = "${decimalPartStr[1]}"
            if(decimalPartStr[0] != '0' || decimalPartStr[1] != '0') newDecimalPartStr = "${decimalPartStr[0]}$newDecimalPartStr"
            if(newDecimalPartStr != "") newDecimalPartStr = ".$newDecimalPartStr"

            retStr = integerPart.toString() + newDecimalPartStr
        }

        retStr += if(!isMetric) " Miles"
        else " Kilometers"

        return retStr
    }

    fun durationToMinSecStr(duration: Double): String {
        val minutes = duration.toInt()
        val secondsStr = '.' + duration.toString().split(".")[1]
        val seconds = (60 * secondsStr.toDouble()).toInt()

        var retStr = ""
        if(minutes != 0) retStr += "$minutes mins "
        retStr += "$seconds secs"

        return retStr
    }

    fun calendarToStr(calendar: Calendar): String {
        val year = calendar.get(Calendar.YEAR).toString()
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())?.take(3)
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString()

        var hour = calendar.get(Calendar.HOUR_OF_DAY).toString()
        if(hour.length == 1) hour = "0$hour"
        var minute = calendar.get(Calendar.MINUTE).toString()
        if(minute.length == 1) minute = "0$minute"
        var second = calendar.get(Calendar.SECOND).toString()
        if(second.length == 1) second = "0$second"

        return "$hour:$minute:$second $month $day $year"
    }
}
