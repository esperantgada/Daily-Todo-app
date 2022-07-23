package eg.esperantgada.dailytodo.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import eg.esperantgada.dailytodo.event.TodoEvent
import eg.esperantgada.dailytodo.model.Category
import eg.esperantgada.dailytodo.model.Todo
import eg.esperantgada.dailytodo.repository.CategoryRepository
import eg.esperantgada.dailytodo.repository.PreferenceRepository
import eg.esperantgada.dailytodo.repository.SortOrder
import eg.esperantgada.dailytodo.repository.TodoRepository
import eg.esperantgada.dailytodo.utils.ADD_TODO_RESULT_OK
import eg.esperantgada.dailytodo.utils.EDIT_TODO_RESULT_OK
import eg.esperantgada.dailytodo.utils.TODO_ADDED_MESSAGE
import eg.esperantgada.dailytodo.utils.TODO_UPDATED_MESSAGE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TA = "CategoryViewModel"

@HiltViewModel
class CategoryViewModel
@Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val todoRepository: TodoRepository,
    private val preferenceRepository: PreferenceRepository,
    state : SavedStateHandle
    ) : ViewModel(){

    /**
     * This will hold the current searchQuery the user type in TodoFragment and handle its state via
     * [SavedStateHandle] which will save it
     */
    val searchQuery = state.getLiveData("searchQuery", "")


    /* val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
     val hideCompleted = MutableStateFlow(false)*/

    /**
     * This is the channel that helps to pass data between coroutines.It can hold a data of type TodoEvent
     * We send in an event and it exposes this event to the fragment as [Flow].It can suspend if the UI or fragment
     * is not ready yet
     */
    private val todoEventChannel = Channel<TodoEvent>()

    //Gets a specific event
    val todoEvent = todoEventChannel.receiveAsFlow()


    /**
     * Gets reference to the [preferenceFlow] defined in the PreferenceRepository
     *
     * Instead of using [sortOrder] and [hideCompleted] here, we replace all of them by [preferenceFlow]
     * in [combine] method and the remaining by [FilterPreference]
     */
    val preferenceFlow = preferenceRepository.preferenceFlow


    /**
     * When the [searchQuery], [sortOrder] and [hideCompleted] change , the results are received from the
     * [database] and stored in [todos] variable
     * Using [combine] allow to put all them together instead of doing it separately
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val todoFlow = combine(
        searchQuery.asFlow(),
        preferenceFlow) {searchQuery, filterPreference ->
        Pair(searchQuery, filterPreference)
    }.flatMapLatest { (searchQuery, filterPreference) ->
        todoRepository.getTodoList(searchQuery,
            filterPreference.sortOrder,
            filterPreference.hideCompleted)
    }

    val todos = todoFlow.cachedIn(viewModelScope)

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferenceRepository.updateSortOrder(sortOrder)
    }

    fun onHideCompleted(hideCompleted : Boolean) = viewModelScope.launch {
        preferenceRepository.updateHideCompleted(hideCompleted)
    }

    //When an item is selected, navigate to edit Fragment to edit the selected item
    fun onTodoSelected(todo : Todo){
        viewModelScope.launch {
            todoEventChannel.send(TodoEvent.GoToEditFragment(todo))
        }
    }

    //When a task is checked, it is updated in the database
    fun onTodoCheckedChanged(
        todo: Todo,
        isChecked : Boolean,
        context: Context
    ) = viewModelScope.launch {
        todoRepository.update(todo.copy(completed = isChecked))
    }

    fun onItemSwiped(todo: Todo){
        viewModelScope.launch {
            todoRepository.delete(todo)
            todoEventChannel.send(TodoEvent.ShowUndoDeleteTodoMessage(todo))
        }
    }

    //This undoes the delete action and insert the task in the database
    fun onUndoDelete(todo: Todo){
        viewModelScope.launch {
            todoRepository.insert(todo)
        }
    }

    //Handles navigation to addFragment
    fun addNewTodo(){
        viewModelScope.launch {
            todoEventChannel.send(TodoEvent.GoToAddTodoFragment)
        }
    }

    //Helps to check if the list is empty and displays empty list message to the user
    val allTodo = todoRepository.getAllTodo().asLiveData()

    //This takes the result value sent by AddEditTodoFragment and shows confirmation message accordingly
    //in the fragment
    fun onAddEditTodoResult(result : Int){
        when (result){
            ADD_TODO_RESULT_OK -> showAddEditTodoConfirmationMessage(TODO_ADDED_MESSAGE)

            EDIT_TODO_RESULT_OK -> showAddEditTodoConfirmationMessage(TODO_UPDATED_MESSAGE)
        }
    }

    //Helper function that sends confirmation message event to the fragment through onAddEditTodoResult method
    private fun showAddEditTodoConfirmationMessage(message : String){
        viewModelScope.launch {
            todoEventChannel.send(TodoEvent.ShowSavedTodoConfirmationMessage(message))
        }
    }

    fun goToAlertDialogFragment(){
        viewModelScope.launch {
            todoEventChannel.send(TodoEvent.GotoAlertDialogFragment)
        }
    }



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

    fun updateCategory(category : Category){
        viewModelScope.launch {
            categoryRepository.updateCategory(category)
        }
    }

    fun getAllTodoByCategoryName(name : String) = categoryRepository.getTodoByCategoryName(name)
}