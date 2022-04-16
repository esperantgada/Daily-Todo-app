package eg.esperantgada.dailytodo.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import eg.esperantgada.dailytodo.model.Todo
import eg.esperantgada.dailytodo.room.TodoDao
import javax.inject.Inject

class TodoRepository @Inject constructor(private val todoDao: TodoDao) {

    suspend fun insert(todo : Todo){
        todoDao.insert(todo)
    }

    suspend fun update(todo: Todo){
        todoDao.update(todo)
    }

    suspend fun delete(todo: Todo){
        todoDao.delete(todo)
    }

    fun getTodoList(
        searchQuery : String,
        sortOrder: SortOrder,
        hideCompleted : Boolean
    ) = Pager(
        PagingConfig(
            pageSize = 20,
            maxSize = 500,
            enablePlaceholders = true
        ), pagingSourceFactory = {todoDao.getTodoList(searchQuery, sortOrder, hideCompleted)}
        ).flow

    fun deleteAllCompletedTodo(){
        todoDao.deleteAllCompletedTodo()
    }
}