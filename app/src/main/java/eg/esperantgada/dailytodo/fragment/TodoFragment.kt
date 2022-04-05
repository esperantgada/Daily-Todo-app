package eg.esperantgada.dailytodo.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import eg.esperantgada.dailytodo.R
import eg.esperantgada.dailytodo.adapter.TodoAdapter
import eg.esperantgada.dailytodo.databinding.FragmentTodoBinding
import eg.esperantgada.dailytodo.model.TodoEntity
import eg.esperantgada.dailytodo.repository.SortOrder
import eg.esperantgada.dailytodo.utils.exhaustive
import eg.esperantgada.dailytodo.utils.onQueryTextChanged
import eg.esperantgada.dailytodo.viewmodel.TodoViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

/**
 * Inject the [TodoViewModel] in the [Fragment]
 */
@AndroidEntryPoint
class TodoFragment : Fragment(), TodoAdapter.OnItemClickedListener {

    private val todoViewModel: TodoViewModel by viewModels()

    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    private lateinit var todosList : MutableList<TodoEntity>
    private lateinit var todoAdapter : TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTodoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todoAdapter = TodoAdapter(this)

        binding.apply {
            recyclerView.apply {
                adapter = todoAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                floating.setOnClickListener { todoViewModel.addNewTodo() }
            }

        }

        todoViewModel.todos.observe(viewLifecycleOwner) {
            todosList = it as MutableList<TodoEntity>
            todoAdapter.submitList(todosList)
        }

        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        /**
         * This will handle the different events action that the ViewModel will send.
         */
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            todoViewModel.todoEvent.collect { event ->
                when(event){
                    is TodoViewModel.TodoEvent.ShowUndoDeleteTodoMessage ->{
                        showSnackBarAndToast(event)
                    }

                    is TodoViewModel.TodoEvent.GoToAddTodoFragment -> {
                        val action = TodoFragmentDirections.actionTodoFragmentToAddEditFragment(null, "Add todo")
                        findNavController().navigate(action)
                    }

                    is TodoViewModel.TodoEvent.GoToEditFragment -> {
                        val action = TodoFragmentDirections.actionTodoFragmentToAddEditFragment(event.todo, "Edit todo")
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }
    }

    //Method that shows SnackBar and Toast messages
    private fun showSnackBarAndToast(event: TodoViewModel.TodoEvent.ShowUndoDeleteTodoMessage) {
        Snackbar.make(requireView(), "REALLY DELETE TASK ?", Snackbar.LENGTH_INDEFINITE)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
            .setAction("UNDO") {
                todoViewModel.onUndoDelete(event.todo)
                Toast.makeText(requireContext(), "Undone successfully", Toast.LENGTH_LONG)
                    .show()
            }.show()
    }

    /**
     * Logic for search in [SearchView]
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.todo_menu, menu)

        val searchedTodo = menu.findItem(R.id.search_todo)
        val searchView = searchedTodo.actionView as SearchView

        searchView.onQueryTextChanged {
            todoViewModel.searchQuery.value = it
        }

        //Read the current hideCompleted state from the DataStore
        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.hide_completed_todo).isChecked =
                todoViewModel.preferenceFlow.first().hideCompleted
        }
    }


    /**
     * Handles all logic for [actionBar] menu
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.sort_by_name -> {
                todoViewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }

            R.id.sort_by_date_created -> {
                todoViewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }

            R.id.hide_completed_todo -> {
                item.isChecked = !item.isChecked
                todoViewModel.onHideCompleted(item.isChecked)
                true
            }

            R.id.delete_all_completed_todo -> {
                true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }


    /**
     * If an item is clicked or if a task is checked, the viewModel updates this one in the database
     */
    override fun onItemClicked(todo: TodoEntity) {
        todoViewModel.onTodoSelected(todo)
    }


    override fun onCheckBoxClicked(todo: TodoEntity, isChecked: Boolean) {
        todoViewModel.onTodoCheckedChanged(todo, isChecked)
    }

    //Sets drag and swipe actions on items in the recycler view
    private val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
        @SuppressLint("NotifyDataSetChanged")
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition

            Collections.swap(todosList, fromPosition, toPosition)
            recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)

            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val todo = todoAdapter.currentList[viewHolder.adapterPosition]
            todoViewModel.onItemSwiped(todo)
        }
    })


    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}