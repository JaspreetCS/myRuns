package com.example.myruns4

import androidx.room.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

@TypeConverters(MyConverters::class)
@Entity(tableName = "manual_table")
data class Manual(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0L,

        @ColumnInfo(name = "type_column")
        var Type: String = "",

        @ColumnInfo(name = "input_column")
        var Input: String = "",

        @ColumnInfo(name = "date_column")
        var Date: String = "",

        @ColumnInfo(name = "time_column")
        var Time: String = "",

        @ColumnInfo(name = "duration_column")
        var Duration: Double = 0.0,

        @ColumnInfo(name = "distance_column")
        var Distance: Double = 0.0,

        @ColumnInfo(name = "calories_column")
        var Calories: Double = 0.0,

        @ColumnInfo(name = "heart_rate_column")
        var Heart_Rate: Int = 0,

        @ColumnInfo(name = "comment_column")
        var Comment: String = "",

        @ColumnInfo(name = "average_pace_column")
        var Avg_Pace: Double = 0.0,

        @ColumnInfo(name = "avg_speed_column")
        var Avg_Speed: Double = 0.0,

        @ColumnInfo(name = "climb_column")
        var Climb: Double = 0.0,

        @ColumnInfo(name = "location_column")
        var locations: ArrayList<LatLng>? = ArrayList()

)


class MyConverters
{
        @TypeConverter
        fun toArrayList(json: String): ArrayList<LatLng>?
        {
                val listType: Type = object : TypeToken<ArrayList<LatLng>>() {}.type
                return Gson().fromJson(json, listType)


        }

        @TypeConverter
        fun fromArrayList(array: ArrayList<LatLng>?): String
        {
                return Gson().toJson(array)
        }
}