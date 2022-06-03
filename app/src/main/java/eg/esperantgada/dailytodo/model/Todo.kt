package eg.esperantgada.dailytodo.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat
import java.time.LocalDateTime


@Entity(tableName = "todo_table")
@Parcelize
data class Todo @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "todo_name")
    val name: String,

    val important: Boolean = false,

    val completed: Boolean = false,

    val switchOn: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "due_date")
    val date: String,

    @ColumnInfo(name = "due_time")
    val time: String,

    @ColumnInfo(name = "task_duration")
    val duration: String,

    @ColumnInfo(name = "task_sound")
    val ringtoneUri: String,

    @ColumnInfo(name = "repeat_frequency")
    val repeatFrequency : List<String>?

) : Parcelable
