package eg.esperantgada.dailytodo.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import eg.esperantgada.dailytodo.model.Category
import eg.esperantgada.dailytodo.room.CategoryDao
import javax.inject.Inject

class CategoryRepository
@Inject constructor(private val categoryDao: CategoryDao) {

    suspend fun insertCategory(category: Category){
        categoryDao.insert(category)
    }

    suspend fun updateCategory(category: Category){
        categoryDao.update(category)
    }

    suspend fun deleteCategory(category: Category){
        categoryDao.delete(category)
    }

    fun getTodoByCategoryName(name: String) = categoryDao.getAllTodoByCategoryName(name)

    fun getAllCategories() = categoryDao.getCategories()


    //val categories = categoryDao.getCategories()



    fun getCategories() = Pager(
        PagingConfig(
            pageSize = 10,
            maxSize = 50,
            enablePlaceholders = true
        ), pagingSourceFactory = {categoryDao.getAllCategories()}
    ).flow
}