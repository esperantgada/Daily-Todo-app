package eg.esperantgada.dailytodo.repository

import eg.esperantgada.dailytodo.model.TodoEntity
import eg.esperantgada.dailytodo.room.TodoDao
import javax.inject.Inject

class TodoRepository @Inject constructor(private val todoDao: TodoDao) {

    suspend fun insert(todo : TodoEntity){
        todoDao.insert(todo)
    }

    suspend fun update(todo: TodoEntity){
        todoDao.update(todo)
    }

    suspend fun delete(todo: TodoEntity){
        todoDao.delete(todo)
    }

    fun getTodoList(searchQuery : String, sortOrder: SortOrder, hideCompleted : Boolean) =
        todoDao.getTodoList(searchQuery, sortOrder, hideCompleted)
}