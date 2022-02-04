package eg.esperantgada.dailytodo.room

import androidx.room.*
import eg.esperantgada.dailytodo.model.TodoEntity
import eg.esperantgada.dailytodo.viewmodel.SortOrder
import kotlinx.coroutines.flow.Flow


/**
 * [searchQuery] represents the argument users will type in the [serachBar]
 * The two % state the beginning or the end character of the [serachQuery]
 */
@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo : TodoEntity)

    @Update
    suspend fun update(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)

    //This method will be called in the ViewModel
    fun getTodoList(query : String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<TodoEntity>> = when(sortOrder){
        SortOrder.BY_NAME -> getTodoListSortedByName(query, hideCompleted)

        SortOrder.BY_DATE -> getTodoListSortedByDate(query, hideCompleted)
    }

    //Handles search BY_NAME
    @Query("SELECT * FROM todo_table WHERE (isCompleted != :hideCompleted OR isCompleted = 0) AND todo_name LIKE '%' || :searchQuery || '%' ORDER BY isImportant DESC, todo_name")
     fun getTodoListSortedByName(searchQuery : String, hideCompleted : Boolean) : Flow<List<TodoEntity>>

     //Handles search BY_DATE
    @Query("SELECT * FROM todo_table WHERE (isCompleted != :hideCompleted OR isCompleted = 0) AND todo_name LIKE '%' || :searchQuery || '%' ORDER BY isImportant DESC, created_at")
    fun getTodoListSortedByDate(searchQuery : String, hideCompleted : Boolean) : Flow<List<TodoEntity>>



}