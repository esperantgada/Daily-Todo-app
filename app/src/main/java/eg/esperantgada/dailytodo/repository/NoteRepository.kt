package eg.esperantgada.dailytodo.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import eg.esperantgada.dailytodo.model.Note
import eg.esperantgada.dailytodo.room.NoteDao
import javax.inject.Inject

class NoteRepository @Inject constructor(private val noteDao: NoteDao) {

    suspend fun insert(note: Note){
        noteDao.insert(note)
    }

    suspend fun update(note: Note){
        noteDao.update(note)
    }

    suspend fun delete(note: Note){
        noteDao.delete(note)
    }

    fun getNoteList(searchQuery : String, sortOrder: SortOrder) = Pager(
        PagingConfig(
            pageSize = 20,
            maxSize = 500,
            enablePlaceholders = true
        ), pagingSourceFactory = {noteDao.getNoteList(searchQuery, sortOrder)}
    ).flow

    fun deleteAllNotes(){
        noteDao.deleteAllNote()
    }

    fun getAllNotes() = noteDao.getAllNote()

}