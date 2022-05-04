package eg.esperantgada.dailytodo.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eg.esperantgada.dailytodo.event.AddEditNoteEvent
import eg.esperantgada.dailytodo.model.Note
import eg.esperantgada.dailytodo.repository.NoteRepository
import eg.esperantgada.dailytodo.utils.ADD_NOTE_RESULT_OK
import eg.esperantgada.dailytodo.utils.EDIT_NOTE_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val state: SavedStateHandle
) : ViewModel(){

    private val addEditNoteChannel = Channel<AddEditNoteEvent>()
    val addEditNoteEvent = addEditNoteChannel.receiveAsFlow()

    //Note sent from note list fragment
   private val sentNote = state.get<Note>("note")

    var noteTitle = state.get<String>("title") ?: sentNote?.title ?: ""
    set(value) {
        field = value
        state.set("title", noteTitle)
    }

    var noteDescription = state.get<String>("description") ?: sentNote?.description ?: ""
    set(value) {
        field = value
        state.set("description", noteDescription)
    }

    //Updates item and navigates back
    private fun updateNote(note: Note) {
        viewModelScope.launch {
            noteRepository.update(note)
            addEditNoteChannel.send(AddEditNoteEvent.GoBackWithResult(EDIT_NOTE_RESULT_OK))
        }
    }


    //Inserts item and navigates back
    private fun insertNote(note: Note) {
        viewModelScope.launch {
            noteRepository.insert(note)
            addEditNoteChannel.send(AddEditNoteEvent.GoBackWithResult(ADD_NOTE_RESULT_OK))
        }
    }

    //Shows invalid input message added
    private fun showInvalidInputMessage(message: String) {
        viewModelScope.launch {
            addEditNoteChannel.send(AddEditNoteEvent.ShowInvalidInputMessage(message))
        }
    }

     fun isInputInvalid() :Boolean {
        return when {
            noteTitle.isBlank() && noteDescription.isNotBlank() -> {
                showInvalidInputMessage("Set a title for your note")
                true
            }
            noteDescription.isBlank() && noteTitle.isNotBlank() -> {
                showInvalidInputMessage("Set a description for your note")
                true
            }
            noteTitle.isBlank() && noteDescription.isBlank() -> {
                showInvalidInputMessage("Note title and description can't be empty")
                true
            }
            else -> false
        }
    }

    //Will be executed when FAB will be clicked in the fragment
    fun onSaveClick() {
        if (isInputInvalid()){
            return
        }else{
            when {
                sentNote != null -> {
                    val updatedNote = sentNote.copy(
                        title = noteTitle,
                        description = noteDescription
                    )
                    updateNote(updatedNote)
                }
                else -> {
                    val newNote = Note(
                        title = noteTitle,
                        description = noteDescription
                    )
                    insertNote(newNote)

                }
            }
        }
    }
}