package eg.esperantgada.dailytodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import eg.esperantgada.dailytodo.model.Category
import eg.esperantgada.dailytodo.repository.CategoryRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel
@Inject constructor(
    private val categoryRepository: CategoryRepository
    ) : ViewModel(){


    //Helps to check if the list is empty and displays empty list message to the user
    val allCategories = categoryRepository.getAllCategories().asLiveData()


    val categoryList = categoryRepository.getCategories().cachedIn(viewModelScope)

    fun addCategory(category: Category){
        viewModelScope.launch {
            categoryRepository.insertCategory(category)

        }
    }

    fun deleteCategory(category: Category){
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }
}