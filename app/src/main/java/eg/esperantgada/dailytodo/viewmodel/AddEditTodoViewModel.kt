package eg.esperantgada.dailytodo.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eg.esperantgada.dailytodo.alarm.TodoAlarm
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
    private val context: Context,
    private val todoRepository: TodoRepository,
    private val state: SavedStateHandle,
) : ViewModel() {
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

    var isImportant = state.get<Boolean>("isImportant") ?: sentTodo?.isImportant ?: false
        set(value) {
            field = value
            state.set("isImportant", isImportant)
        }

    var todoDate = state.get<String>("date") ?: sentTodo?.date ?: ""
        set(value) {
            field = value
            state.set("todoDate", todoDate)
        }

    var todoTime = state.get<String>("todoTime") ?: sentTodo?.time ?: ""
        set(value) {
            field = value
            state.set("todoTime", todoTime)
        }

    //Updates item and navigates back
    private fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.update(todo)
            addEditTodoEventChannel.send(AddEditTodoEvent.GoBackWithResult(EDIT_TODO_RESULT_OK))
        }
    }


    //Inserts item and navigates back
    private fun insertTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.insert(todo)
            addEditTodoEventChannel.send(AddEditTodoEvent.GoBackWithResult(ADD_TODO_RESULT_OK))
        }
    }

    //Shows invalid input message added
    private fun showInvalidInputMessage(message: String) {
        viewModelScope.launch {
            addEditTodoEventChannel.send(AddEditTodoEvent.ShowInvalidInputMessage(message))
        }
    }

    private fun isInputInvalid() :Boolean {
        when {
            todoName.isBlank() && todoDate.isNotBlank() && todoTime.isNotBlank() -> {
                showInvalidInputMessage(INVALID_INPUT_MESSAGE)
                return true
            }
            todoName.isBlank() && todoDate.isBlank() && todoTime.isNotBlank() -> {
                showInvalidInputMessage("Task name and task date cannot be empty")
                return true
            }
            todoName.isBlank() && todoTime.isBlank() && todoDate.isNotBlank() -> {
                showInvalidInputMessage("Task name and task time cannot be empty")
                return true
            }
            todoDate.isBlank() && todoName.isNotBlank() && todoTime.isNotBlank() -> {
                showInvalidInputMessage("Task date cannot be empty")
                return true
            }
            todoDate.isBlank() && todoTime.isBlank() && todoName.isNotBlank() -> {
                showInvalidInputMessage("Task date and time cannot be empty")
                return true
            }
            todoTime.isBlank() && todoName.isNotBlank() && todoDate.isNotBlank() -> {
                showInvalidInputMessage("Task time cannot be empty")
                return true
            }

            todoName.isBlank() && todoDate.isBlank() && todoTime.isBlank() -> {
                showInvalidInputMessage("Task name, date and time cannot be empty")
                return true
            }
            else -> return false
        }
    }

    //Will be executed when FAB will be clicked in the fragment
    fun onSaveClick() {
        if (isInputInvalid()){
            return
        }else{
            when {
                sentTodo != null -> {
                    val updatedTodo = sentTodo.copy(
                        name = todoName,
                        isImportant = isImportant,
                        date = todoDate,
                        time = todoTime
                    )
                    updateTodo(updatedTodo)
                    TodoAlarm.setTodoAlarmReminder(context, updatedTodo)
                }
                else -> {
                    val newTodo = Todo(
                        name = todoName,
                        isImportant = isImportant,
                        date = todoDate,
                        time = todoTime)
                    insertTodo(newTodo)
                    TodoAlarm.setTodoAlarmReminder(context, newTodo)
                }

            }
        }
    }
}