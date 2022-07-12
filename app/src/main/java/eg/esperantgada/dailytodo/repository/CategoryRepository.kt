package eg.esperantgada.dailytodo.repository

import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import eg.esperantgada.dailytodo.model.Category
import eg.esperantgada.dailytodo.room.CategoryDao
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val categoryDao: CategoryDao) {

    suspend fun insertCategory(category: Category){
        categoryDao.insert(category)
    }

    suspend fun updateCategory(category: Category){
        categoryDao.update(category)
    }

    suspend fun deleteCategory(category: Category){
        categoryDao.delete(category)
    }

    fun getCategoryByName(name: String) = categoryDao.getCategoryByName(name)

    fun getAllCategories() = categoryDao.getCategories()



    fun getCategories() = Pager(
        PagingConfig(
            pageSize = 20,
            maxSize = 50,
            enablePlaceholders = true
        ), pagingSourceFactory = {categoryDao.getAllCategories()}
    )

    fun getAllTodoByCategoryName() = Pager(
        PagingConfig(
            pageSize = 20,
            maxSize = 50,
            enablePlaceholders = true
        ), pagingSourceFactory = {categoryDao.getAllCategories()}
    )

}