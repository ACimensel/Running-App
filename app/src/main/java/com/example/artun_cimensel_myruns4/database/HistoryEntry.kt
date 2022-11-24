package com.example.artun_cimensel_myruns4.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.util.*

@Entity(tableName = "history_table")
data class HistoryEntry(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "inputType_column")
    var inputType: Int = 0,

    @ColumnInfo(name = "activityType_column")
    var activityType: Int = 0,

    @ColumnInfo(name = "dateTime_column")
    var dateTime: Calendar = Calendar.getInstance(),

    @ColumnInfo(name = "duration_column")
    var duration: Double = 0.0,

    @ColumnInfo(name = "distance_column")
    var distance: Double = 0.0,

    @ColumnInfo(name = "avgPace_column")
    var avgPace: Double = 0.0,

    @ColumnInfo(name = "avgSpeed_column")
    var avgSpeed: Double = 0.0,

    @ColumnInfo(name = "calorie_column")
    var calorie: Double = 0.0,

    @ColumnInfo(name = "climb_column")
    var climb: Double = 0.0,

    @ColumnInfo(name = "heartRate_column")
    var heartRate: Double = 0.0,

    @ColumnInfo(name = "comment_column")
    var comment: String = "",

    @ColumnInfo(name = "locationList_column")
    var locationList: ArrayList<LatLng>? = null
)