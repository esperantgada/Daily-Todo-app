package eg.esperantgada.dailytodo.viewmodel

import android.provider.ContactsContract
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import eg.esperantgada.dailytodo.dependencyinjection.ApplicationScope
import eg.esperantgada.dailytodo.model.Note
import eg.esperantgada.dailytodo.repository.NoteRepository
import eg.esperantgada.dailytodo.repository.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertDialogViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val noteRepository: NoteRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    fun onConfirmDeleteTodo(){
        applicationScope.launch {
            todoRepository.deleteAllCompletedTodo()
        }
    }


    //This method belongs to Note

    fun onConfirmDeleteAllNote(){
        applicationScope.launch {
            noteRepository.deleteAllNotes()
        }
    }
}