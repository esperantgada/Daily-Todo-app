package eg.esperantgada.dailytodo.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Parcelize
@Entity(tableName = "note_table")
data class Note(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val noteId : Int = 0,

    @ColumnInfo(name = "note_title")
    val title : String,

    @ColumnInfo(name = "note_description")
    val description : String,

    @ColumnInfo(name = "note_date")
    val createdDate : Long = System.currentTimeMillis()
): Parcelable{

    val dateFormatted : String
        get() = DateFormat.getDateTimeInstance().format(createdDate)
}
