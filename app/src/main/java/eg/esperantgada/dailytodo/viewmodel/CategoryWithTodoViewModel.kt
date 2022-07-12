package eg.esperantgada.dailytodo.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import eg.esperantgada.dailytodo.event.CategoryAndTodoEvent
import eg.esperantgada.dailytodo.repository.CategoryRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class CategoryWithTodoViewModel
@Inject constructor(
    private val categoryRepository: CategoryRepository
    ) : ViewModel(){

}