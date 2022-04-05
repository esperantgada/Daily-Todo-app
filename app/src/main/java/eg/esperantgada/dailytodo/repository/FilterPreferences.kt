package eg.esperantgada.dailytodo.repository

data class FilterPreferences(
    val sortOrder: SortOrder,
    val hideCompleted : Boolean
    )
