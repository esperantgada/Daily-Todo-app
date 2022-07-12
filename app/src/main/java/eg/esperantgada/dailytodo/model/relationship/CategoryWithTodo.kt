package eg.esperantgada.dailytodo.model.relationship

import androidx.room.Embedded
import androidx.room.Relation
import eg.esperantgada.dailytodo.model.Category
import eg.esperantgada.dailytodo.model.Todo

data class CategoryWithTodo(

    @Embedded val category: Category,

    @Relation(
        parentColumn = "category_name",
        entityColumn = "category_name"
    )
    val todos: List<Todo>

)
