package eg.esperantgada.dailytodo.event

sealed class AddEditNoteEvent{
    data class ShowInvalidInputMessage(val message : String) : AddEditNoteEvent()
    data class GoBackWithResult(val result : Int) : AddEditNoteEvent()
}
