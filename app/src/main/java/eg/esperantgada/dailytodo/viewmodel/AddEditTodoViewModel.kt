package eg.esperantgada.dailytodo.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eg.esperantgada.dailytodo.event.AddEditTodoEvent
import eg.esperantgada.dailytodo.model.Todo
import eg.esperantgada.dailytodo.repository.TodoRepository
import eg.esperantgada.dailytodo.utils.ADD_TODO_RESULT_OK
import eg.esperantgada.dailytodo.utils.EDIT_TODO_RESULT_OK
import eg.esperantgada.dailytodo.utils.INVALID_INPUT_MESSAGE
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val state : SavedStateHandle
    ) : ViewModel(){

    private val addEditTodoEventChannel = Channel<AddEditTodoEvent>()
    val addEditTodoEvent = addEditTodoEventChannel.receiveAsFlow()

    //Retrieve the data sent from TodoFragment and save it in the state so that that it doesn't lost
    val sentTodo = state.get<Todo>("todo")

    //This takes a task's name and save it in the state instance as key/value
    var todoName = state.get<String>("todoName") ?: sentTodo?.name ?: ""
    set(value) {
        field = value
        state.set("todoName", value)
    }

    var isImportant = state.get<Boolean>("isCompleted") ?: sentTodo?.isImportant ?: false
    set(value) {
        field = value
        state.set("isCompleted", isImportant)
    }

    //Updates item and navigates back
    private fun updateTodo(todo : Todo){
        viewModelScope.launch {
            todoRepository.update(todo)
            addEditTodoEventChannel.send(AddEditTodoEvent.GoBackWithResult(EDIT_TODO_RESULT_OK))
        }
    }


    //Inserts item and navigates back
    private fun insertTodo(todo: Todo){
        viewModelScope.launch {
            todoRepository.insert(todo)
            addEditTodoEventChannel.send(AddEditTodoEvent.GoBackWithResult(ADD_TODO_RESULT_OK))
        }
    }

    //Shows invalid input message added
    private fun showInvalidInputMessage(message: String){
        viewModelScope.launch {
            addEditTodoEventChannel.send(AddEditTodoEvent.ShowInvalidInputMessage(message))
        }
    }

    //Will be executed when FAB will be clicked in the fragment
    fun onSaveClick(){
        if (todoName.isBlank()){
            showInvalidInputMessage(INVALID_INPUT_MESSAGE)
            return
        }

        //If the received item is not null, take in account the changes made and update it else create another one
        if (sentTodo != null){
            val updatedTodo = sentTodo.copy(name = todoName, isImportant = isImportant)
            updateTodo(updatedTodo)
        }else{
            val newTodo = Todo(name = todoName, isImportant = isImportant)
            insertTodo(newTodo)
        }
    }

}