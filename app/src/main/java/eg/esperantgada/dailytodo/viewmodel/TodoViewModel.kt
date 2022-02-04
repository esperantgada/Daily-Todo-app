package eg.esperantgada.dailytodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import eg.esperantgada.dailytodo.room.TodoDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * Inject [todoDao] in the [ViewModel]
 */

@HiltViewModel
class TodoViewModel @Inject constructor(private val todoDao: TodoDao) : ViewModel(){

    /**
     * This will hold the current searchQuery the user type in TodoFragment
     */
    val searchQuery = MutableStateFlow("")

    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted = MutableStateFlow(false)


    /**
     * When the [searchQuery], [sortOrder] and [hideCompleted] change , the results are received from the
     * [database] and stored in [todos] variable
     * Using [combine] allow to put all them together instead of doing it separately
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val todoFlow = combine(searchQuery, sortOrder, hideCompleted) {searchQuery, sortOrder, hideCompleted ->
        Triple(searchQuery, sortOrder, hideCompleted)
    }.flatMapLatest { (searchQuery, sortOrder, hideCompleted) ->
        todoDao.getTodoList(searchQuery, sortOrder, hideCompleted)
    }

    val todos = todoFlow.asLiveData()

}

enum class SortOrder{BY_NAME, BY_DATE}