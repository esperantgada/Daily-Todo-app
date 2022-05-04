package eg.esperantgada.dailytodo.event

import eg.esperantgada.dailytodo.model.Note

sealed class NoteEvent{
    object GoToAddNoteFragment : NoteEvent()
    data class GoToEditNoteFragment(val note: Note) : NoteEvent()
    data class ShowUndoDeleteNoteMessage(val note: Note) : NoteEvent()
    data class ShowSavedNoteConfirmationMessage(val message: String) : NoteEvent()
    data class ShareNote(val note: Note) : NoteEvent()
    data class DeleteNote(val note: Note) : NoteEvent()
    object GotoAlertDialogFragment : NoteEvent()

}
