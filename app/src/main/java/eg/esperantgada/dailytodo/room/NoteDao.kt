package eg.esperantgada.dailytodo.room

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.*
import eg.esperantgada.dailytodo.model.Note
import eg.esperantgada.dailytodo.repository.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("DELETE FROM note_table")
    fun deleteAllNote()

    @Query("SELECT * FROM note_table")
    fun getAllNote() : Flow<List<Note>>

    @Query("SELECT * FROM note_table WHERE note_title LIKE '%' || :searchQuery || '%' ORDER BY note_title")
    fun getNoteByTitle(searchQuery : String) : PagingSource<Int, Note>

    @Query("SELECT * FROM note_table WHERE note_title LIKE '%' || :searchQuery || '%' ORDER BY note_date")
    fun getAllNoteByDate(searchQuery: String) : PagingSource<Int, Note>

    fun getNoteList(searchQuery: String, sortOrder: SortOrder) : PagingSource<Int, Note> = when(sortOrder){
        SortOrder.BY_TITLE -> getNoteByTitle(searchQuery)

        SortOrder.BY_DATE -> getAllNoteByDate(searchQuery)

        else -> getNoteByTitle(searchQuery)
    }
}