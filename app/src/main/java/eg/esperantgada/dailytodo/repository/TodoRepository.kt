package eg.esperantgada.dailytodo.repository

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

    fun getTodoList(searchQuery : String, sortOrder: SortOrder, hideCompleted : Boolean) =
        todoDao.getTodoList(searchQuery, sortOrder, hideCompleted)

    fun deleteAllCompletedTodo(){
        todoDao.deleteAllCompletedTodo()
    }
}