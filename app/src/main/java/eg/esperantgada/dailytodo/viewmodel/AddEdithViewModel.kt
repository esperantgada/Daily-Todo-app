package eg.esperantgada.dailytodo.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import eg.esperantgada.dailytodo.model.TodoEntity
import eg.esperantgada.dailytodo.repository.TodoRepository
import javax.inject.Inject

@HiltViewModel
class AddEdithViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val state : SavedStateHandle
    ) : ViewModel(){

    //Retrieve the data sent from TodoFragment and save it in the state so that that it doesn't lost
    val sentTodo = state.get<TodoEntity>("todo")

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
}