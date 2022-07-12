package eg.esperantgada.dailytodo.event

import eg.esperantgada.dailytodo.model.Category

sealed class CategoryEvent {

    data class GoToCategoryFragment(val category: Category) : CategoryEvent()
    object GoToCreateNewCategoryDialog: CategoryEvent()
}