package eg.esperantgada.dailytodo.event

import eg.esperantgada.dailytodo.model.Todo

//A sealed class is like an enum class but it can holds data
sealed class TodoEvent{
    object GoToAddTodoFragment : TodoEvent()
    data class GoToEditFragment(val todo: Todo) : TodoEvent()
    data class ShowUndoDeleteTodoMessage(val todo: Todo) :TodoEvent()
    data class ShowSavedTodoConfirmationMessage(val message: String) : TodoEvent()
    object GotoAlertDialogFragment : TodoEvent()

}