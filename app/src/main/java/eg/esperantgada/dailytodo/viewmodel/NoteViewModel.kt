package eg.esperantgada.dailytodo.viewmodel

import androidx.lifecycle.*
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import eg.esperantgada.dailytodo.event.NoteEvent
import eg.esperantgada.dailytodo.event.TodoEvent
import eg.esperantgada.dailytodo.model.Note
import eg.esperantgada.dailytodo.model.Todo
import eg.esperantgada.dailytodo.repository.NoteRepository
import eg.esperantgada.dailytodo.repository.PreferenceRepository
import eg.esperantgada.dailytodo.repository.SortOrder
import eg.esperantgada.dailytodo.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val preferenceRepository: PreferenceRepository,
    state : SavedStateHandle
) : ViewModel(){

    val searchQuery = state.getLiveData("searchQuery", "")

    val notes = noteRepository.getAllNotes().asLiveData()

    val preferenceFlow = preferenceRepository.preferenceFlow

    private val noteEventChannel = Channel<NoteEvent>()
    val noteEvent = noteEventChannel.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val noteFlow = combine(
        searchQuery.asFlow(),
        preferenceFlow) {searchQuery, filterPreference ->
        Pair(searchQuery, filterPreference)
    }.flatMapLatest { (searchQuery, filterPreference) ->
        noteRepository.getNoteList(searchQuery,
            filterPreference.sortOrder)
    }


    val notesList = noteFlow.cachedIn(viewModelScope)

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferenceRepository.updateSortOrder(sortOrder)
    }

    //This undoes the delete action and insert the task in the database
    fun onUndoDelete(note: Note){
        viewModelScope.launch {
            noteRepository.insert(note)
        }
    }

    //Handles navigation to addFragment
    fun addNewNote(){
        viewModelScope.launch {
            noteEventChannel.send(NoteEvent.GoToAddNoteFragment)
        }
    }

    //Helps to check if the list is empty and displays empty list message to the user
    val allNotes = noteRepository.getAllNotes().asLiveData()

    //This takes the result value sent by AddEditNoteFragment and shows confirmation message accordingly
    //in the fragment
    fun onAddEditNoteResult(result : Int){
        when (result){
            ADD_NOTE_RESULT_OK -> showAddEditNoteConfirmationMessage("Note added successfully")

            EDIT_NOTE_RESULT_OK -> showAddEditNoteConfirmationMessage("Note updated successfully")
        }
    }

    //Helper function that sends confirmation message event to the fragment through onAddEditTodoResult method
    private fun showAddEditNoteConfirmationMessage(message : String){
        viewModelScope.launch {
            noteEventChannel.send(NoteEvent.ShowSavedNoteConfirmationMessage(message))
        }
    }

    fun goToAlertDialogFragment(){
        viewModelScope.launch {
            noteEventChannel.send(NoteEvent.GotoAlertDialogFragment)
        }
    }


    //When an item is selected, navigate to edit Fragment to edit the selected item
    fun onNoteSelected(note: Note){
        viewModelScope.launch {
            noteEventChannel.send(NoteEvent.GoToEditNoteFragment(note))
        }
    }

    fun onDeleteClicked(note: Note){
        viewModelScope.launch {
            noteRepository.delete(note)
            noteEventChannel.send(NoteEvent.ShowUndoDeleteNoteMessage(note))
        }
    }

    fun onShareNoteClicked(note: Note){
        viewModelScope.launch {
            noteEventChannel.send(NoteEvent.ShareNote(note))
        }
    }
}