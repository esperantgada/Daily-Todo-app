package eg.esperantgada.dailytodo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import eg.esperantgada.dailytodo.event.CategoryEvent
import eg.esperantgada.dailytodo.event.TodoEvent
import eg.esperantgada.dailytodo.model.Category
import eg.esperantgada.dailytodo.model.Todo
import eg.esperantgada.dailytodo.repository.CategoryRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel
@Inject constructor(
    private val categoryRepository: CategoryRepository
    ) : ViewModel(){

    //Helps to check if the list is empty and displays empty list message to the user
    val allCategories = categoryRepository.getAllCategories().asLiveData()

    val categoryList = categoryRepository.getCategories().flow.cachedIn(viewModelScope)


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

    fun updateCategory(category: Category){
        viewModelScope.launch {
            categoryRepository.updateCategory(category)
        }
    }

    fun getSingleCategoryByName(name : String) = categoryRepository.getCategoryByName(name)
}