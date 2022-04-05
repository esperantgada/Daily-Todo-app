package eg.esperantgada.dailytodo.viewmodel

import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import eg.esperantgada.dailytodo.model.TodoEntity
import eg.esperantgada.dailytodo.repository.PreferenceRepository
import eg.esperantgada.dailytodo.repository.SortOrder
import eg.esperantgada.dailytodo.repository.TodoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Inject [todoDao] in the [ViewModel]
 */

@Suppress("KDocUnresolvedReference")
@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val preferenceRepository: PreferenceRepository,
    private val state : SavedStateHandle
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

    val todos = todoFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferenceRepository.updateSortOrder(sortOrder)
    }

    fun onHideCompleted(hideCompleted : Boolean) = viewModelScope.launch {
        preferenceRepository.updateHideCompleted(hideCompleted)
    }

    //When an item is selected, navigate to edit Fragment to edit the selected item
    fun onTodoSelected(todo : TodoEntity){
        viewModelScope.launch {
            todoEventChannel.send(TodoEvent.GoToEditFragment(todo))
        }
    }

    //When a task is checked, it is updated in the database
    fun onTodoCheckedChanged(
        todo: TodoEntity,
        isChecked : Boolean
    ) = viewModelScope.launch { 
        todoRepository.update(todo.copy(isCompleted = isChecked))
    }

    fun onItemSwiped(todo: TodoEntity){
        viewModelScope.launch {
            todoRepository.delete(todo)
            todoEventChannel.send(TodoEvent.ShowUndoDeleteTodoMessage(todo))
        }
    }

    //This undoes the delete action and insert the task in the database
    fun onUndoDelete(todo: TodoEntity){
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


    //A sealed class is like an enum class but it can holds data
    sealed class TodoEvent{
        object GoToAddTodoFragment : TodoEvent()
        data class GoToEditFragment(val todo: TodoEntity) : TodoEvent()
        data class ShowUndoDeleteTodoMessage(val todo: TodoEntity) :TodoEvent()

    }
}
