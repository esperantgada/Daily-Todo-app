package eg.esperantgada.dailytodo.event

sealed class AddEditTodoEvent{
    data class ShowInvalidInputMessage(val message : String) : AddEditTodoEvent()
    data class GoBackWithResult(val result : Int) : AddEditTodoEvent()
}