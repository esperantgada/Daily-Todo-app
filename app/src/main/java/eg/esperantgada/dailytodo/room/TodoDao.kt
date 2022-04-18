package eg.esperantgada.dailytodo.room

import androidx.paging.PagingSource
import androidx.room.*
import eg.esperantgada.dailytodo.model.Todo
import eg.esperantgada.dailytodo.repository.SortOrder
import kotlinx.coroutines.flow.Flow


/**
 * [searchQuery] represents the argument users will type in the [serachBar]
 * The two % state the beginning or the end character of the [serachQuery]
 */
@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo : Todo)

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    //This method will be called in the ViewModel
    fun getTodoList(query : String, sortOrder: SortOrder, hideCompleted: Boolean): PagingSource<Int, Todo> = when(sortOrder){
        SortOrder.BY_NAME -> getTodoListSortedByName(query, hideCompleted)

        SortOrder.BY_DATE -> getTodoListSortedByDate(query, hideCompleted)
    }

    //Handles search BY_NAME
    @Query("SELECT * FROM todo_table WHERE (isCompleted != :hideCompleted OR isCompleted = 0) AND todo_name LIKE '%' || :searchQuery || '%' ORDER BY isImportant DESC, todo_name")
     fun getTodoListSortedByName(searchQuery : String, hideCompleted : Boolean) : PagingSource<Int, Todo>

     //Handles search BY_DATE
    @Query("SELECT * FROM todo_table WHERE (isCompleted != :hideCompleted OR isCompleted = 0) AND todo_name LIKE '%' || :searchQuery || '%' ORDER BY isImportant DESC, created_at")
    fun getTodoListSortedByDate(searchQuery : String, hideCompleted : Boolean) : PagingSource<Int, Todo>

    @Query("DELETE  FROM todo_table WHERE isCompleted = 1")
    fun deleteAllCompletedTodo()

    @Query("SELECT * FROM TODO_TABLE")
    fun getAllTodo() : Flow<List<Todo>>



}