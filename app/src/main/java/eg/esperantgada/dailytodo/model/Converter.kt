package eg.esperantgada.dailytodo.model

import android.annotation.SuppressLint
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class Converter {

    @TypeConverter
    fun fromTimestampToDate(time : Long) : Date{
        return Date(time)
    }

    @TypeConverter
    fun fromDateToLong(date: Date) : Long{
        return date.time
    }
}