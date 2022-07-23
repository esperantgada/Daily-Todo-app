package eg.esperantgada.dailytodo.room

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import eg.esperantgada.dailytodo.model.Category
import eg.esperantgada.dailytodo.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Update
    suspend fun update(category: Category)

    @Query("SELECT * FROM category_table")
    fun getAllCategories() : PagingSource<Int, Category>

    @Query("SELECT * FROM todo_table WHERE category_name =:name")
    fun getAllTodoByCategoryName(name : String) : LiveData<List<Todo>>

    @Query("SELECT * FROM category_table")
    fun getCategories() : Flow<List<Category>>


}