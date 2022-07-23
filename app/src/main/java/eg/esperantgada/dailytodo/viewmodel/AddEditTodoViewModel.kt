package eg.esperantgada.dailytodo.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eg.esperantgada.dailytodo.alarm.TodoAlarm
import eg.esperantgada.dailytodo.event.AddEditTodoEvent
import eg.esperantgada.dailytodo.model.Category
import eg.esperantgada.dailytodo.model.Todo
import eg.esperantgada.dailytodo.repository.TodoRepository
import eg.esperantgada.dailytodo.utils.ADD_TODO_RESULT_OK
import eg.esperantgada.dailytodo.utils.EDIT_TODO_RESULT_OK
import eg.esperantgada.dailytodo.utils.INVALID_INPUT_MESSAGE
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

const val TAG = "AddEditTodoViewModel"

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val state: SavedStateHandle,
) : ViewModel() {

    private val addEditTodoEventChannel = Channel<AddEditTodoEvent>()
    val addEditTodoEvent = addEditTodoEventChannel.receiveAsFlow()


    private var _days = MutableLiveData<List<String>>()

     fun setDay(sentDay : List<String>){
        _days.value = sentDay
    }


    //The local time that will be stored in the database
    @SuppressLint("NewApi")
    private val currentDateTime = LocalDateTime.now()
    @SuppressLint("NewApi")
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")
    @SuppressLint("NewApi")
     val formattedDate  = currentDateTime.format(formatter)



    //Retrieve the data sent from TodoFragment and save it in the state so that that it doesn't lost
    private val sentTodo = state.get<Todo>("todo")
    private val sentCategory = state.get<Category>("category")

    //This takes a task's name and save it in the state instance as key/value
    var todoName = state.get<String>("todoName") ?: sentTodo?.name ?: ""
        set(value) {
            field = value
            state["todoName"] = value
        }

    var categoryName = state.get<String>("name") ?: sentCategory?.categoryName ?: ""
        set(value) {
            field = value
            state["name"] = value
        }


    var isImportant = state.get<Boolean>("isImportant") ?: sentTodo?.important ?: false
        set(value) {
            field = value
            state["isImportant"] = isImportant
        }

    var todoDate = state.get<String>("date") ?: sentTodo?.date ?: ""
        set(value) {
            field = value
            state["todoDate"] = todoDate
        }

    var todoTime = state.get<String>("todoTime") ?: sentTodo?.time ?: ""
        set(value) {
            field = value
            state["todoTime"] = todoTime
        }

    var todoDuration = state.get<String>("todoDuration") ?: sentTodo?.duration ?: ""
    set(value) {
        field = value
        state["todoDuration"] = todoDuration
    }


    var todoRingtoneUri = state.get<String>("ringtoneUri") ?: sentTodo?.ringtoneUri ?: ""
        set(value) {
            field = value
            state["ringtoneUri"] = todoRingtoneUri
        }

    var days = state.get<List<String>>("frequency") ?: sentTodo?.repeatFrequency ?: arrayListOf()
    set(value) {
        field = value
        state["frequency"] = days
    }


    var isSwitchOn = state.get<Boolean>("switch") ?: sentTodo?.switchOn ?: false
    set(value) {
        field = value
        state["switch"] = isSwitchOn
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
    fun onSaveClick(context: Context) {
        if (isInputInvalid()){
            return
        }else{
            when {
                sentTodo != null -> {
                    val updatedTodo = sentTodo.copy(
                        categoryName = categoryName,
                        important = isImportant,
                        date = todoDate,
                        time = todoTime,
                        duration = todoDuration,
                        ringtoneUri = todoRingtoneUri,
                        createdAt = formattedDate,
                        switchOn = isSwitchOn,
                        repeatFrequency = days,
                        name = todoName
                    )
                    updateTodo(updatedTodo)

                    TodoAlarm.setTodoAlarmReminder(context, updatedTodo)
                    Log.d(TAG, "UPDATED DAY LIST IN VIEWMODEL: ${_days.value}")

                }
                else -> {
                    val newTodo = Todo(
                        categoryName = categoryName,
                        important = isImportant,
                        date = todoDate,
                        time = todoTime,
                        duration = todoDuration,
                        ringtoneUri = todoRingtoneUri,
                        createdAt = formattedDate,
                        switchOn = isSwitchOn,
                        repeatFrequency = days,
                        name = todoName
                    )

                    insertTodo(newTodo)


                    Log.d(TAG, "NEW DAY LIST IN VIEWMODEL: ${_days.value}")
                    Log.d(TAG, "TODO NAME IN ADDEDITTODOFRAGMENT : $todoName")
                    TodoAlarm.setTodoAlarmReminder(context, newTodo)
                }
            }
        }
    }
}