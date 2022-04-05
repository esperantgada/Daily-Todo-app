package eg.esperantgada.dailytodo.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat


@Entity(tableName = "todo_table")
@Parcelize
data class TodoEntity(
    @ColumnInfo(name = "todo_name")
    val name : String,

    val isImportant : Boolean = false,
    val isCompleted : Boolean = false,

    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    
    @ColumnInfo(name = "created_at")
    val createdAt : Long = System.currentTimeMillis()
) : Parcelable{
    val dataFormatted : String
        get() = DateFormat.getDateTimeInstance().format(createdAt)
}