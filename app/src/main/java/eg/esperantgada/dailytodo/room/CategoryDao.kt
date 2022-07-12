package eg.esperantgada.dailytodo.room

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import eg.esperantgada.dailytodo.model.Category
import eg.esperantgada.dailytodo.model.Todo
import eg.esperantgada.dailytodo.model.relationship.CategoryWithTodo
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


    /*@Query("SELECT * FROM category_table WHERE category_name =:categoryName")
    fun getAllTodoByCategory(categoryName : String) : PagingSource<Int, CategoryWithTodo>
*/

    @Transaction
    @Query("SELECT * FROM category_table WHERE category_name =:categoryName")
    fun getCategoryByName(categoryName : String) : LiveData<CategoryWithTodo>

    @Query("SELECT * FROM category_table")
    fun getCategories() : Flow<List<Category>>


}