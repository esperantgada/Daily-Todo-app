package eg.esperantgada.dailytodo.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "category_table")
@Parcelize
data class Category(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "category_name")
    var categoryName : String,

    @ColumnInfo(name = "category_image")
    var image : Int,

    @ColumnInfo(name = "category_color")
    val color : Int,

    @ColumnInfo(name = "category_icon")
    var icon:Int

) : Parcelable
