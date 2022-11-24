package com.example.artun_cimensel_myruns4.database

import androidx.room.TypeConverter
import com.example.artun_cimensel_myruns4.Util
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class Converters{
    @TypeConverter
    fun fromCalendar(calendar: Calendar): String {
        return Util.calendarToStr(calendar)
    }

    @TypeConverter
    fun toCalendar(calendarStr: String): Calendar {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("HH:mm:ss MMM dd yyyy", Locale.ENGLISH)
        calendar.time = sdf.parse(calendarStr)

        return calendar
    }

    @TypeConverter
    fun fromStringToArrayList(string: String?): ArrayList<LatLng>? {
        val listType = object: TypeToken<ArrayList<LatLng>>(){}.type

        return if(string != null)
            Gson().fromJson(string, listType)
        else
            null
    }

    @TypeConverter
    fun fromArrayListToString(arrayList: ArrayList<LatLng>?): String? {
        return if(arrayList != null)
            Gson().toJson(arrayList)
        else
            null
    }
}
