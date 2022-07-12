package eg.esperantgada.dailytodo.event

import eg.esperantgada.dailytodo.model.Category
import eg.esperantgada.dailytodo.model.Todo

sealed class CategoryAndTodoEvent{

    object GoToAddTodoFragment : CategoryAndTodoEvent()
    data class GoToEditFragment(val todo: Todo) : CategoryAndTodoEvent()
}