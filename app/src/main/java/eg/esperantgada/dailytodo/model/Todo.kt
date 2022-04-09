package eg.esperantgada.dailytodo.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat


@Entity(tableName = "todo_table")
@Parcelize
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,

    @ColumnInfo(name = "todo_name")
    val name : String,

    val isImportant : Boolean = false,
    val isCompleted : Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt : Long = System.currentTimeMillis(),

    @ColumnInfo(name = "due_date")
    val date : String,

    @ColumnInfo(name = "due_time")
    val time : String

) : Parcelable{
    val dataFormatted : String
        get() = DateFormat.getDateTimeInstance().format(createdAt)
}